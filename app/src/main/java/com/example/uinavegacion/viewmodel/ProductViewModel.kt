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

/**
 * Representa el estado de la UI para la pantalla de productos.
 * AHORA INCLUYE LA LISTA DE PRODUCTOS INACTIVOS.
 */
data class ProductUiState(
    val productDetails: ProductEntity? = null,
    val isEntry: Boolean = true,
    // --- Nuevos campos para la lista de archivados ---
    val inactiveProducts: List<ProductEntity> = emptyList(),
    val isInactiveListLoading: Boolean = false,
    val inactiveListError: String? = null
)

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    // --- SIN CAMBIOS ---
    // La lista de productos ACTIVOS. Se actualiza automáticamente desde la base de datos.
    val products: StateFlow<List<ProductEntity>> = productRepository.getAllProducts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // --- SIN CAMBIOS ---
    // El resto de tus funciones existentes (crear, borrar, etc.)
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

    // --- ¡NUEVAS FUNCIONES AÑADIDAS! ---

    /**
     * Pide al repositorio la lista de productos INACTIVOS y actualiza el estado.
     */
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

    /**
     * Llama al repositorio para restaurar un producto (cambiar su estado a ACTIVE).
     */
    fun restoreProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.restoreProduct(product)
            // Después de restaurar, la lista de ACTIVOS se actualizará sola (porque es un Flow).
            // Pero debemos refrescar manualmente la lista de INACTIVOS para que el producto desaparezca de ella.
            loadInactiveProducts()
        }
    }
}