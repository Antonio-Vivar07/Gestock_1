package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.uinavegacion.GestockApplication

/**
 * El proveedor único para toda la aplicación. Proporciona una forma estándar de crear cualquier ViewModel.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Inicializador para AuthViewModel
        initializer {
            AuthViewModel(
                gestockApplication().container.userRepository
            )
        }

        // Inicializador para ProductViewModel
        initializer {
            ProductViewModel(
                gestockApplication().container.productRepository
            )
        }
    }
}

/**
 * Función de extensión para obtener la instancia de la aplicación desde dentro de un ViewModel.
 */
fun CreationExtras.gestockApplication(): GestockApplication = 
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GestockApplication)
