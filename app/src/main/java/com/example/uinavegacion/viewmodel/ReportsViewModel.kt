package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.remote.RemoteProductReport
import com.example.uinavegacion.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ReportsViewModel(
    private val repo: ProductRepository
) : ViewModel() {

    private val _report = MutableStateFlow<List<RemoteProductReport>>(emptyList())
    val report: StateFlow<List<RemoteProductReport>> = _report.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadReport() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                // ✅ FIX REAL: tu repo tiene getReport(), NO getProductsReport()
                _report.value = repo.getReport()
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }
}
