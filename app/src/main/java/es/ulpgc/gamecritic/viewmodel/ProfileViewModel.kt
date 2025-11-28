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

    var currentUid by mutableStateOf<String?>(null)
        private set

    var userComments by mutableStateOf<List<Comment>>(emptyList())
        private set

    var isLoadingComments by mutableStateOf(false)
        private set

    var commentsError by mutableStateOf<String?>(null)
        private set

    var isFollowed by mutableStateOf(false)
        private set

    var isLoadingFollowAction by mutableStateOf(false)
        private set

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

    var isSavingProfile by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            val uid = userRepository.getCurrentUserId()
            currentUid = uid
            if (uid != null) {
                user = userRepository.getUserProfile(uid)
                editingProfileImageBase64 = user?.profileIcon
                loadUserComments(uid)
                isFollowed = false
            }
        }
    }

    fun setEditingProfileImage(base64: String?) {
        editingProfileImageBase64 = base64
    }

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

    fun loadFollowers(profileId: String? = user?.id) {
        val pid = profileId ?: return
        viewModelScope.launch {
            isLoadingFollowers = true
            followersList.clear()
            try {
                val profile = withContext(Dispatchers.IO) { userRepository.getUserProfile(pid) }
                val ids = profile?.followers ?: emptyList()
                for (fid in ids) {
                    try {
                        val u = withContext(Dispatchers.IO) { userRepository.getUserProfile(fid) }
                        if (u != null) {
                            followersList.add(UserSummary(id = u.id, name = u.name, username = u.username, icon = u.profileIcon))
                        }
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {
            } finally {
                isLoadingFollowers = false
            }
        }
    }

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
                isSavingProfile = true
                try {
                    userRepository.updateUserProfileWithImage(
                        uid = uid,
                        name = name,
                        username = username,
                        description = description,
                        base64Image = editingProfileImageBase64
                    )

                    user = user?.copy(
                        name = name,
                        username = username,
                        description = description,
                        profileIcon = editingProfileImageBase64 ?: user?.profileIcon ?: ""
                    )

                    onComplete(true)
                } catch (e: Exception) {
                    onComplete(false)
                } finally {
                    isSavingProfile = false
                }
            } else {
                onComplete(false)
            }
        }
    }

    fun follow(onComplete: (Boolean) -> Unit = {}) {
        val targetId = user?.id ?: run { onComplete(false); return }
        viewModelScope.launch(Dispatchers.IO) {
            val currentUid = userRepository.getCurrentUserId()
            if (currentUid == null) { onComplete(false); return@launch }
            if (currentUid == targetId) { onComplete(false); return@launch }
            isLoadingFollowAction = true
            try {
                userRepository.followUser(currentUid, targetId)
                isFollowed = true
                user = userRepository.getUserProfile(targetId)
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            } finally {
                isLoadingFollowAction = false
            }
        }
    }

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
