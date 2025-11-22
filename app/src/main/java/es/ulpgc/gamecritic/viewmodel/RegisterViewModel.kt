package es.ulpgc.gamecritic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ulpgc.gamecritic.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(name: String, username: String, email: String, password: String, repeatPassword: String) {
        if (name.isBlank() || username.isBlank() || email.isBlank() || password.isBlank() || repeatPassword.isBlank()) {
            _registerState.value = RegisterState.Error("Todos los campos son obligatorios")
            return
        }
        if (password != repeatPassword) {
            _registerState.value = RegisterState.Error("Las contraseñas no coinciden")
            return
        }
        _registerState.value = RegisterState.Loading
        viewModelScope.launch {
            val result = repository.register(name, username, email, password)
            if (result.isSuccess) {
                _registerState.value = RegisterState.Success
            } else {
                _registerState.value = RegisterState.Error(result.exceptionOrNull()?.localizedMessage ?: "Error desconocido")
            }
        }
    }
}
