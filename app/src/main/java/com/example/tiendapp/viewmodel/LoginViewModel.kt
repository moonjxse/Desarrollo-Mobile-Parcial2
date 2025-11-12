package com.example.tiendapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tiendapp.data.AppDatabase
import com.example.tiendapp.data.User
import com.example.tiendapp.model.UserRepository
import com.example.tiendapp.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage: StateFlow<String?> = _loginMessage.asStateFlow()

    private val _loginSuccess = MutableStateFlow<Boolean?>(null)
    val loginSuccess: StateFlow<Boolean?> = _loginSuccess.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun loginUser() {
        val email = _email.value.trim()
        val password = _password.value.trim()

        if (!ValidationUtils.isValidEmail(email)) {
            _loginMessage.value = "Correo electrónico inválido"
            return
        }
        if (password.isBlank()) {
            _loginMessage.value = "Debe ingresar su contraseña"
            return
        }

        viewModelScope.launch {
            val user: User? = repository.getByEmail(email)
            if (user == null) {
                _loginMessage.value = "El usuario no existe"
                _loginSuccess.value = false
            } else if (user.password != password) {
                _loginMessage.value = "Contraseña incorrecta"
                _loginSuccess.value = false
            } else {
                _loginMessage.value = "Inicio de sesión exitoso"
                _loginSuccess.value = true
            }
        }
    }

    fun clearMessage() {
        _loginMessage.value = null
    }

    class Factory(private val application: Application) :
        ViewModelProvider.AndroidViewModelFactory(application) {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
