package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import com.example.uinavegacion.repository.PostRepository
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.uinavegacion.GestockApplication
import com.example.uinavegacion.viewmodel.PostViewModel

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


        // Inicializador para PostViewModel (consumo de API REST)
        initializer {
            PostViewModel(
                gestockApplication().container.postRepository
            )
        }

        // Inicializador para ReportsViewModel
        initializer {
            ReportsViewModel(
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