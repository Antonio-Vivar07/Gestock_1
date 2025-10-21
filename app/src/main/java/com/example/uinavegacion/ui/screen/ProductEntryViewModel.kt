package com.example.uinavegacion.ui.screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// --- VIEWMODEL CORREGIDO ---
class ProductEntryViewModel : ViewModel() {
    // La variable que la pantalla espera encontrar.
    var selectedProduct by mutableStateOf<Product?>(null)
}
