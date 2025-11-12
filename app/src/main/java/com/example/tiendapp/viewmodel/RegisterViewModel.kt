package com.example.tiendapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendapp.data.AppDatabase
import com.example.tiendapp.data.User
import com.example.tiendapp.model.UserRepository
import com.example.tiendapp.utils.ValidationUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _registrationSuccess = MutableStateFlow<Boolean?>(null)
    val registrationSuccess: StateFlow<Boolean?> = _registrationSuccess.asStateFlow()

    fun onNameChange(newName: String) { _name.value = newName }
    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }

    fun registerUser() {
        val name = _name.value.trim()
        val email = _email.value.trim()
        val password = _password.value.trim()

        when {
            !ValidationUtils.isValidNombre(name) -> _message.value = "Nombre inválido"
            !ValidationUtils.isValidEmail(email) -> _message.value = "Correo electrónico inválido"
            password.length < 6 -> _message.value = "La contraseña debe tener al menos 6 caracteres"
            else -> viewModelScope.launch {
                val existingUser = repository.getByEmail(email)
                if (existingUser != null) {
                    _message.value = "El correo ya está registrado"
                    _registrationSuccess.value = false
                } else {
                    val newUser = User(
                        name = name,
                        email = email,
                        password = password,
                        isAdmin = false
                    )
                    repository.insertUser(newUser)
                    _message.value = "Usuario registrado exitosamente"
                    _registrationSuccess.value = true
                }
            }
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
