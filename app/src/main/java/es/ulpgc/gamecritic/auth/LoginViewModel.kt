package es.ulpgc.gamecritic.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel(private val repository: LoginRepository = LoginRepository()) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.isSuccess) {
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Error(result.exceptionOrNull()?.localizedMessage ?: "Error desconocido")
            }
        }
    }
}