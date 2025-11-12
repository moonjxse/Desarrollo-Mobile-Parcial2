package com.example.tiendapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tiendapp.data.AppDatabase
import com.example.tiendapp.data.User
import com.example.tiendapp.model.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
    }

    val allUsers = repository.allUsers

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            if (user != null) {
                _currentUser.value = user
                _message.value = "Inicio de sesión exitoso"
            } else {
                _message.value = "Credenciales incorrectas"
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _message.value = "Sesión cerrada"
    }

    fun registerUser(user: User) {
        viewModelScope.launch {
            repository.insertUser(user)
            _message.value = "Usuario registrado correctamente"
        }
    }

    fun updateProfile(updatedUser: User) {
        viewModelScope.launch {
            repository.updateUser(updatedUser)
            _currentUser.value = updatedUser
            _message.value = "Perfil actualizado correctamente"
        }
    }

    fun clearMessage() {
        _message.value = null
    }
}
