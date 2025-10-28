package com.example.uinavegacion.repository

import com.example.uinavegacion.data.local.movimiento.MovimientoDao
import com.example.uinavegacion.data.local.movimiento.MovimientoEntity
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductDao
import com.example.uinavegacion.data.local.product.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao, private val movimientoDao: MovimientoDao) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun getProductByCode(code: String): ProductEntity? = productDao.findByCode(code)

    suspend fun createProduct(name: String, code: String, description: String, category: String, zone: String, minStock: Int) {
        // --- CORRECCIÓN DEFINITIVA: Uso de argumentos con nombre para evitar errores de orden ---
        val productEntity = ProductEntity(
            name = name,
            code = code,
            description = description,
            category = category,
            zone = zone,
            minStock = minStock
        )
        productDao.insertProduct(productEntity)
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    suspend fun handleStockMovement(product: ProductEntity, type: MovimientoType, quantity: Int, user: String, refDoc: String?, reason: String?, date: Long) {
        val delta: Int
        val newStock: Int

        when (type) {
            MovimientoType.INGRESO -> {
                delta = quantity
                newStock = product.stock + quantity
            }
            MovimientoType.EGRESO -> {
                delta = -quantity
                newStock = (product.stock - quantity).coerceAtLeast(0)
            }
            MovimientoType.AJUSTE -> {
                delta = quantity - product.stock
                newStock = quantity
            }
            MovimientoType.TRANSFERENCIA -> {
                return
            }
        }

        val movimiento = MovimientoEntity(
            productId = product.id,
            type = type,
            quantity = delta,
            user = user,
            referenceDocument = refDoc,
            reason = reason,
            createdAt = date
        )
        
        movimientoDao.insertMovimiento(movimiento)
        productDao.updateStock(product.id, newStock)
    }
}