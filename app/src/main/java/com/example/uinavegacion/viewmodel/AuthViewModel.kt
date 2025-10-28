package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class UserRole { ADMINISTRADOR, TRABAJADOR }

data class UserSession(val username: String, val role: UserRole)

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _session = MutableStateFlow<UserSession?>(null)
    val session = _session.asStateFlow()

    fun login(username: String, pass: String, onLoginResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.loginUser(username, pass)
            if (user != null) {
                _session.value = UserSession(user.username, user.role)
                onLoginResult(true)
            } else {
                onLoginResult(false)
            }
        }
    }

    fun register(username: String, email: String, pass: String, role: UserRole, onRegisterResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = userRepository.registerUser(username, email, pass, role)
            onRegisterResult(success)
        }
    }

    fun logout() {
        _session.value = null
    }
}