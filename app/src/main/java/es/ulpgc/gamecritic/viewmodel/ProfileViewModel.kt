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
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.withContext

// Resumen pequeño de usuario para mostrar en listas de seguidores/seguidos
data class UserSummary(
    val id: String,
    val name: String?,
    val username: String?,
    val icon: String?
)

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

    // Listas para diálogo de seguidores / seguidos
    var followersList = mutableStateListOf<UserSummary>()
        private set
    var followingList = mutableStateListOf<UserSummary>()
        private set

    var isLoadingFollowers by mutableStateOf(false)
        private set
    var isLoadingFollowing by mutableStateOf(false)
        private set

    var editingProfileImageBase64 by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            val uid = userRepository.getCurrentUserId()
            currentUid = uid
            if (uid != null) {
                user = userRepository.getUserProfile(uid)
                editingProfileImageBase64 = user?.profileIcon
                loadUserComments(uid)
                isFollowed = false // perfil propio no se marca como seguido
            }
        }
    }

    fun setEditingProfileImage(base64: String?) {
        editingProfileImageBase64 = base64
    }

    // Cargar un perfil arbitrario por id (por ejemplo, cuando navegamos al perfil de otro usuario)
    fun loadProfileById(profileId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentUid == null) {
                    currentUid = userRepository.getCurrentUserId()
                }
                user = userRepository.getUserProfile(profileId)
                editingProfileImageBase64 = user?.profileIcon
                loadUserComments(profileId)
                isFollowed = if (currentUid != null && currentUid != profileId) {
                    userRepository.isFollowing(currentUid!!, profileId)
                } else {
                    false
                }
            } catch (_: Exception) {
            }
        }
    }

    // Cargar la lista de seguidores (UserSummary) para mostrar en diálogo
    fun loadFollowers(profileId: String? = user?.id) {
        val pid = profileId ?: return
        viewModelScope.launch {
            isLoadingFollowers = true
            followersList.clear()
            try {
                // obtener perfil para acceder a la lista de ids
                val profile = withContext(Dispatchers.IO) { userRepository.getUserProfile(pid) }
                val ids = profile?.followers ?: emptyList()
                // obtener cada user resumen
                for (fid in ids) {
                    try {
                        val u = withContext(Dispatchers.IO) { userRepository.getUserProfile(fid) }
                        if (u != null) {
                            followersList.add(UserSummary(id = u.id, name = u.name, username = u.username, icon = u.profileIcon))
                        }
                    } catch (_: Exception) {
                        // ignorar usuario individual si falla
                    }
                }
            } catch (_: Exception) {
                // manejar error (silencioso)
            } finally {
                isLoadingFollowers = false
            }
        }
    }

    // Cargar la lista de usuarios que sigue el perfil
    fun loadFollowing(profileId: String? = user?.id) {
        val pid = profileId ?: return
        viewModelScope.launch {
            isLoadingFollowing = true
            followingList.clear()
            try {
                val profile = withContext(Dispatchers.IO) { userRepository.getUserProfile(pid) }
                val ids = profile?.following ?: emptyList()
                for (fid in ids) {
                    try {
                        val u = withContext(Dispatchers.IO) { userRepository.getUserProfile(fid) }
                        if (u != null) {
                            followingList.add(UserSummary(id = u.id, name = u.name, username = u.username, icon = u.profileIcon))
                        }
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {

            } finally {
                isLoadingFollowing = false
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
                    editingProfileImageBase64?.let {
                        userRepository.updateUserProfileImage(uid, it)
                    }
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
