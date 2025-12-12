package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProductUiState(
    val productDetails: ProductEntity? = null,
    val isEntry: Boolean = true,
    val inactiveProducts: List<ProductEntity> = emptyList(),
    val isInactiveListLoading: Boolean = false,
    val inactiveListError: String? = null
)

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    val products: StateFlow<List<ProductEntity>> = productRepository.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun createProduct(name: String, code: String, description: String, category: String, zone: String, minStock: Int) {
        viewModelScope.launch {
            productRepository.createProduct(name, code, description, category, zone, minStock)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    suspend fun getProductByCode(code: String): ProductEntity? {
        return productRepository.getProductByCode(code)
    }

    fun handleStockMovement(product: ProductEntity, type: MovimientoType, quantity: Int, user: String, refDoc: String?, reason: String?) {
        viewModelScope.launch {
            productRepository.handleStockMovement(product, type, quantity, user, refDoc, reason, System.currentTimeMillis())
        }
    }

    fun loadInactiveProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isInactiveListLoading = true, inactiveListError = null) }
            try {
                val inactives = productRepository.getInactiveProducts()
                _uiState.update { currentState ->
                    currentState.copy(isInactiveListLoading = false, inactiveProducts = inactives)
                }
            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(isInactiveListLoading = false, inactiveListError = "Error al cargar productos archivados")
                }
            }
        }
    }

    fun restoreProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.restoreProduct(product)
        }
    }
    
    // --- ¡NUEVA FUNCIÓN DE SINCRONIZACIÓN! ---
    suspend fun syncProducts() {
        productRepository.syncWithBackend()
    }
}
