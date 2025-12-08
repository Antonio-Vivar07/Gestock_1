package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.ProductReport
import com.example.uinavegacion.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(private val productRepository: ProductRepository) : ViewModel() {

    // Flujo para la lista de productos del reporte
    private val _productReports = MutableStateFlow<List<ProductReport>>(emptyList())
    val productReports: StateFlow<List<ProductReport>> = _productReports

    // Flujo para el estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Flujo para mensajes de error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadReports()
    }

    /**
     * Carga los datos del reporte desde el repositorio.
     */
    fun loadReports() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                _productReports.value = productRepository.getProductsReport()
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar el reporte: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
