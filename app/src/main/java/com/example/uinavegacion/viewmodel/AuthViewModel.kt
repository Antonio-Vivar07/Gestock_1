package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class UserRole { ADMINISTRADOR, TRABAJADOR }

data class UserSession(val username: String, val role: UserRole)

class AuthViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _session = MutableStateFlow<UserSession?>(null)
    val session: StateFlow<UserSession?> = _session.asStateFlow()

    // Lista reactiva de usuarios (para tu pantalla de Gestión)
    val users: StateFlow<List<UserEntity>> = userRepository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun login(username: String, pass: String, onLoginResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = userRepository.loginUser(username, pass)
            if (user != null) {
                _session.value = UserSession(user.username, user.role)

                val roleName = user.role.name.lowercase()
                    .replaceFirstChar { it.titlecase() }

                SessionManager.userInfo = "${user.username} ($roleName)"
                onLoginResult(true)
            } else {
                onLoginResult(false)
            }
        }
    }

    fun register(
        username: String,
        email: String,
        pass: String,
        role: UserRole,
        onRegisterResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = userRepository.registerUser(username, email, pass, role)
            onRegisterResult(success)
        }
    }

    fun logout() {
        _session.value = null
        SessionManager.userInfo = null
    }

    // Sincroniza usuarios desde backend -> DB local
    suspend fun syncUsers() {
        userRepository.syncUsersWithBackend()
    }

    // Cambia rol (backend) y luego refresca lista
    fun updateUserRole(
        userToUpdate: UserEntity,
        newRole: UserRole,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val success = userRepository.updateUserRole(userToUpdate, newRole)
            if (success) {
                syncUsers()
            }
            onResult(success)
        }
    }
}
