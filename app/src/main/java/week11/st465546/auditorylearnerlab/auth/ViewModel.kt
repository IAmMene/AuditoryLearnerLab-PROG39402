package week11.st465546.auditorylearnerlab.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ViewModel(
    private val repo: Repository = Repository()
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state

    fun isUserLoggedIn() = repo.currentUser() != null

    fun updateEmail(v: String) {
        _state.value = _state.value.copy(email = v)
    }

    fun updatePassword(v: String) {
        _state.value = _state.value.copy(password = v)
    }

    fun login(onSuccess: () -> Unit) {
        val (email, pass) = _state.value
        if (email.isBlank() || pass.isBlank()) {
            _state.value = _state.value.copy(error = "Email and password required")
            return
        }

        _state.value = _state.value.copy(loading = true, error = null)

        viewModelScope.launch {
            val result = repo.login(email, pass)
            _state.value = _state.value.copy(loading = false)

            if (result.isSuccess) onSuccess()
            else _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
        }
    }

    fun register(onSuccess: () -> Unit) {
        val (email, pass) = _state.value
        if (email.isBlank() || pass.isBlank()) {
            _state.value = _state.value.copy(error = "Fields cannot be empty")
            return
        }

        _state.value = _state.value.copy(loading = true, error = null)

        viewModelScope.launch {
            val result = repo.register(email, pass)
            _state.value = _state.value.copy(loading = false)

            if (result.isSuccess) onSuccess()
            else _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
        }
    }

    fun resetPassword(onSent: () -> Unit) {
        val email = _state.value.email

        if (email.isBlank()) {
            _state.value = _state.value.copy(error = "Enter your email")
            return
        }

        _state.value = _state.value.copy(loading = true, error = null)

        viewModelScope.launch {
            val result = repo.sendPasswordReset(email)
            _state.value = _state.value.copy(loading = false)

            if (result.isSuccess) onSent()
            else _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
        }
    }

    fun logout() = repo.logout()
}