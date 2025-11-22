package es.ulpgc.gamecritic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.repository.UserRepository
import es.ulpgc.gamecritic.model.User
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class ProfileViewModel : ViewModel() {
    var user by mutableStateOf<User?>(null)
        private set

    private val userRepository = UserRepository()

    init {
        viewModelScope.launch {
            val uid = userRepository.getCurrentUserId()
            if (uid != null) {
                user = userRepository.getUserProfile(uid)
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
}
