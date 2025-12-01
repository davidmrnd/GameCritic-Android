package es.ulpgc.gamecritic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.model.User
import es.ulpgc.gamecritic.repository.CommentRepository
import es.ulpgc.gamecritic.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FollowingViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val commentRepository = CommentRepository()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments.asStateFlow()

    // Nuevo: agrupación por username (clave simple para identificar al usuario en UI)
    private val _groupedComments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
    val groupedComments: StateFlow<Map<String, List<Comment>>> = _groupedComments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadFollowingComments() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val currentUid = userRepository.getCurrentUserId()
                if (currentUid == null) {
                    _error.value = "No se ha iniciado sesión"
                    _isLoading.value = false
                    return@launch
                }
                val user = withContext(Dispatchers.IO) { userRepository.getUserProfile(currentUid) }
                val followingIds = user?.following ?: emptyList()
                val allComments = mutableListOf<Comment>()
                for (uid in followingIds) {
                    val comments = withContext(Dispatchers.IO) { commentRepository.getCommentsForUser(uid) }
                    allComments.addAll(comments)
                }
                val sorted = allComments.sortedByDescending { it.createdAt }.take(20)
                _comments.value = sorted

                // Nuevo: agrupar por username para mostrar carruseles por usuario
                _groupedComments.value = sorted.groupBy { it.username ?: "Desconocido" }

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
