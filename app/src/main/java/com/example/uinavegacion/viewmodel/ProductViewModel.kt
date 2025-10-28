package com.example.uinavegacion.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.repository.ProductRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {

    val allProducts: StateFlow<List<ProductEntity>> = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createProduct(name: String, code: String, description: String, category: String, zone: String, minStock: Int) {
        viewModelScope.launch { productRepository.createProduct(name, code, description, category, zone, minStock) }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch { productRepository.deleteProduct(product) }
    }

    // --- FUNCIÓN ACTUALIZADA PARA ACEPTAR FECHA ---
    fun handleStockMovement(product: ProductEntity, type: MovimientoType, quantity: Int, user: String, refDoc: String?, reason: String?, date: Long) {
        viewModelScope.launch { productRepository.handleStockMovement(product, type, quantity, user, refDoc, reason, date) }
    }

    suspend fun getProductByCode(code: String): ProductEntity? {
        return productRepository.getProductByCode(code)
    }
}