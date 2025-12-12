package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para obtener y exponer la lista de usuarios a la UI.
 */
class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Expone un StateFlow con la lista de usuarios, que se actualiza automáticamente.
    val users: StateFlow<List<UserEntity>> = userRepository.getAllUsers()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )
}
