package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel para obtener y exponer la lista de usuarios a la UI.
 * No rompe lógica existente.
 */
class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    // 🔹 Usuarios desde Room (flujo reactivo)
    val users: StateFlow<List<UserEntity>> =
        userRepository.getAllUsers()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // 🔹 Estados útiles para UI (mejora NO invasiva)
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /**
     * 🔹 Método opcional para forzar recarga futura
     * (no rompe nada aunque no se use hoy)
     */
    fun refreshUsers() {
        viewModelScope.launch {
            try {
                _loading.value = true
                _error.value = null
                // Aquí NO llamamos nada extra porque Room ya emite cambios
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
