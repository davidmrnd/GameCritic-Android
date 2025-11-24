package es.ulpgc.gamecritic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.repository.UserRepository
import es.ulpgc.gamecritic.model.User
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import es.ulpgc.gamecritic.model.Comment
import es.ulpgc.gamecritic.repository.CommentRepository
import kotlinx.coroutines.Dispatchers

class ProfileViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val commentRepository = CommentRepository()

    var user by mutableStateOf<User?>(null)
        private set

    var userComments by mutableStateOf<List<Comment>>(emptyList())
        private set

    var isLoadingComments by mutableStateOf(false)
        private set

    var commentsError by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            val uid = userRepository.getCurrentUserId()
            if (uid != null) {
                user = userRepository.getUserProfile(uid)
                loadUserComments(uid)
            }
        }
    }

    fun loadUserComments(userId: String = user?.id.orEmpty()) {
        if (userId.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            isLoadingComments = true
            commentsError = null
            try {
                userComments = commentRepository.getCommentsForUser(userId)
            } catch (e: Exception) {
                userComments = emptyList()
                commentsError = e.message
            } finally {
                isLoadingComments = false
            }
        }
    }

    val name get() = user?.name ?: "Sin nombre"
    val username get() = user?.username ?: "Sin usuario"
    val email get() = user?.email ?: "Sin email"
    val profileIcon get() = user?.profileIcon ?: ""
    val description get() = user?.description ?: ""
    val followingCount get() = user?.following?.size ?: 0
    val followersCount get() = user?.followers?.size ?: 0

    fun editProfile(name: String, username: String, description: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val uid = userRepository.getCurrentUserId()
            if (uid != null) {
                try {
                    userRepository.updateUserProfile(uid, name, username, description)
                    user = userRepository.getUserProfile(uid)
                    onComplete(true)
                } catch (e: Exception) {
                    onComplete(false)
                }
            } else {
                onComplete(false)
            }
        }
    }
}
