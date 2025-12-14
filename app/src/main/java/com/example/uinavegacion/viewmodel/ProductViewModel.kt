package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.data.remote.RemoteProductReport
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

<<<<<<< Updated upstream
    fun createProduct(name: String, code: String, description: String, category: String, zone: String, minStock: Int) {
        viewModelScope.launch {
            productRepository.createProduct(name, code, description, category, zone, minStock)
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
=======
    private val _deletedProducts = MutableStateFlow<List<ProductEntity>>(emptyList())
    val deletedProducts = _deletedProducts.asStateFlow()

    private val _report = MutableStateFlow<List<RemoteProductReport>>(emptyList())
    val report = _report.asStateFlow()

    fun refreshProducts() {
        viewModelScope.launch {
            try { productRepository.refreshProductsFromBackend() } catch (_: Exception) { }
        }
    }

    fun loadDeletedProducts() {
        viewModelScope.launch {
            try { _deletedProducts.value = productRepository.refreshDeletedProductsFromBackend() } catch (_: Exception) { }
        }
    }

    fun deleteRemote(product: ProductEntity, onError: (String) -> Unit = {}) {
        val remoteId = product.remoteId
        if (remoteId.isNullOrBlank()) {
            onError("Producto sin id remoto (sincroniza primero).")
            return
        }
        viewModelScope.launch {
            try {
                productRepository.deleteRemoteProduct(remoteId)
            } catch (e: Exception) {
                onError("No tienes permisos para borrar o hubo un error.")
            }
        }
    }

    fun restoreRemote(product: ProductEntity, onError: (String) -> Unit = {}) {
        val remoteId = product.remoteId
        if (remoteId.isNullOrBlank()) {
            onError("Producto sin id remoto (sincroniza primero).")
            return
        }
        viewModelScope.launch {
            try {
                productRepository.restoreRemoteProduct(remoteId)
                // refresca activos
                productRepository.refreshProductsFromBackend()
            } catch (e: Exception) {
                onError("No tienes permisos para restaurar o hubo un error.")
            }
        }
    }

    fun createProduct(
        name: String,
        code: String,
        description: String,
        category: String,
        zone: String,
        minStock: Int
    ) {
        val product = ProductEntity(
            name = name,
            code = code,
            description = description,
            category = category,
            zone = zone,
            minStock = minStock,
            stock = 0
        )
        addProduct(product)
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch { productRepository.addProduct(product) }
    }

    fun addMovement(
        product: ProductEntity,
        quantity: Int,
        type: MovimientoType,
        refDoc: String,
        reason: String
    ) {
        // Compatibilidad: si se llama desde pantallas antiguas
        val username = "unknown"
        viewModelScope.launch {
            productRepository.addMovement(
                product = product,
                quantity = quantity,
                type = type,
                refDoc = refDoc,
                reason = reason,
                user = username,
                createdAtMillis = System.currentTimeMillis()
            )
        }
    }

    fun handleStockMovement(
        product: ProductEntity,
        type: MovimientoType,
        quantity: Int,
        user: String,
        refDoc: String,
        reason: String,
        date: Long
    ) {
        viewModelScope.launch {
            productRepository.addMovement(
                product = product,
                quantity = quantity,
                type = type,
                refDoc = refDoc,
                reason = reason,
                user = user,
                createdAtMillis = date
            )
        }
    }


    fun loadReport() {
        viewModelScope.launch {
            try {
                _report.value = productRepository.getReport()
            } catch (_: Exception) { }
>>>>>>> Stashed changes
        }
    }

    suspend fun getProductByCode(code: String): ProductEntity? {
        return productRepository.getAllProducts().stateIn(viewModelScope, SharingStarted.Eagerly, emptyList()).value
            .firstOrNull { it.code == code }
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
