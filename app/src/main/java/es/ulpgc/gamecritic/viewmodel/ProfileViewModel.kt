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

    // ID del usuario en sesión
    var currentUid by mutableStateOf<String?>(null)
        private set

    var userComments by mutableStateOf<List<Comment>>(emptyList())
        private set

    var isLoadingComments by mutableStateOf(false)
        private set

    var commentsError by mutableStateOf<String?>(null)
        private set

    // Nuevo: si el usuario en sesión sigue al perfil mostrado
    var isFollowed by mutableStateOf(false)
        private set

    var isLoadingFollowAction by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val uid = userRepository.getCurrentUserId()
            currentUid = uid
            if (uid != null) {
                user = userRepository.getUserProfile(uid)
                loadUserComments(uid)
                isFollowed = false // perfil propio no se marca como seguido
            }
        }
    }

    // Cargar un perfil arbitrario por id (por ejemplo, cuando navegamos al perfil de otro usuario)
    fun loadProfileById(profileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentUid == null) {
                    currentUid = userRepository.getCurrentUserId()
                }
                user = userRepository.getUserProfile(profileId)
                loadUserComments(profileId)
                isFollowed = if (currentUid != null && currentUid != profileId) {
                    userRepository.isFollowing(currentUid!!, profileId)
                } else {
                    false
                }
            } catch (_: Exception) {
                // mantener comportamiento silencioso; user seguirá siendo null
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
            } catch (_: Exception) {
                userComments = emptyList()
                commentsError = null
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

    // Seguir al usuario mostrado
    fun follow(onComplete: (Boolean) -> Unit = {}) {
        val targetId = user?.id ?: run { onComplete(false); return }
        viewModelScope.launch(Dispatchers.IO) {
            val currentUid = userRepository.getCurrentUserId()
            if (currentUid == null) { onComplete(false); return@launch }
            if (currentUid == targetId) { onComplete(false); return@launch }
            isLoadingFollowAction = true
            try {
                userRepository.followUser(currentUid, targetId)
                // actualizar estado localmente
                isFollowed = true
                // refrescar user para actualizar contadores
                user = userRepository.getUserProfile(targetId)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            } finally {
                isLoadingFollowAction = false
            }
        }
    }

    // Dejar de seguir al usuario mostrado
    fun unfollow(onComplete: (Boolean) -> Unit = {}) {
        val targetId = user?.id ?: run { onComplete(false); return }
        viewModelScope.launch(Dispatchers.IO) {
            val currentUid = userRepository.getCurrentUserId()
            if (currentUid == null) { onComplete(false); return@launch }
            if (currentUid == targetId) { onComplete(false); return@launch }
            isLoadingFollowAction = true
            try {
                userRepository.unfollowUser(currentUid, targetId)
                isFollowed = false
                user = userRepository.getUserProfile(targetId)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            } finally {
                isLoadingFollowAction = false
            }
        }
    }
}
