package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.uinavegacion.data.remote.RemoteUser

enum class UserRole { ADMINISTRADOR, TRABAJADOR }

data class UserSession(val username: String, val role: UserRole)

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _session = MutableStateFlow<UserSession?>(null)
    val session = _session.asStateFlow()

    private val _remoteUsers = MutableStateFlow<List<RemoteUser>>(emptyList())
    val remoteUsers = _remoteUsers.asStateFlow()

    private val _usersLoading = MutableStateFlow(false)
    val usersLoading = _usersLoading.asStateFlow()

    private val _usersError = MutableStateFlow<String?>(null)
    val usersError = _usersError.asStateFlow()

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
        viewModelScope.launch {
            userRepository.clearSession()
            _session.value = null
        }
    }

    fun loadRemoteUsers() {
        viewModelScope.launch {
            _usersLoading.value = true
            _usersError.value = null
            try {
                _remoteUsers.value = userRepository.fetchRemoteUsers()
            } catch (e: Exception) {
                _usersError.value = "No se pudieron cargar los usuarios"
            } finally {
                _usersLoading.value = false
            }
        }
    }

    fun setUserRole(username: String, newRole: UserRole, onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val ok = userRepository.updateRemoteUserRole(username, newRole)
            if (ok) {
                // Refrescar lista
                try {
                    _remoteUsers.value = userRepository.fetchRemoteUsers()
                } catch (_: Exception) {}
            }
            onDone(ok)
        }
    }
}