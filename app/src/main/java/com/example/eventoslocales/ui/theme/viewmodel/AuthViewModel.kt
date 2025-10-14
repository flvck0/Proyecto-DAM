package com.example.eventoslocales.ui.theme.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _errorMessage.value = null
    }


    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _errorMessage.value = null

            if (_email.value.isBlank() || !_email.value.contains("@") || _email.value.length < 5) {
                _errorMessage.value = "no es un email válido amigx."
            } else {
                onSuccess()
            }
        }
    }
}