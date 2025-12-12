package com.example.uinavegacion.repository

import android.util.Log
import com.example.uinavegacion.data.local.movimiento.MovimientoDao
import com.example.uinavegacion.data.local.movimiento.MovimientoEntity
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductDao
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.data.remote.InventoryApiService
import com.example.uinavegacion.data.remote.MovementApiService
import com.example.uinavegacion.data.remote.ProductReport
import com.example.uinavegacion.data.remote.RemoteMovement
import com.example.uinavegacion.data.remote.RemoteProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ProductRepository(
    private val productDao: ProductDao,
    private val movimientoDao: MovimientoDao,
    private val inventoryApiService: InventoryApiService,
    private val movementApiService: MovementApiService
) {

    fun getAllProducts(): Flow<List<ProductEntity>> {
        return productDao.getAllProducts()
    }

    suspend fun createProduct(name: String, code: String, description: String, category: String, zone: String, minStock: Int) {
        val newProduct = ProductEntity(
            name = name, code = code, description = description, category = category, zone = zone,
            minStock = minStock, stock = 0, lastUpdate = System.currentTimeMillis()
        )
        productDao.insertProduct(newProduct)

        try {
            val remoteProductPayload = RemoteProduct(
                nombre = name, codigoQr = code, stockActual = 0, categoria = category, ubicacion = zone, minStock = minStock
            )
            val syncedProduct = inventoryApiService.createProduct(remoteProductPayload)
            syncedProduct.id?.let { remoteId ->
                val localProduct = productDao.findByCode(code)
                localProduct?.let {
                    val updatedLocalProduct = it.copy(remoteId = remoteId)
                    productDao.insertProduct(updatedLocalProduct)
                    Log.i("ProductRepository", "Producto '$name' sincronizado y ID remoto ($remoteId) guardado.")
                }
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error al sincronizar nuevo producto con el backend", e)
        }
    }
    
    // --- ¡FUNCIÓN RESTAURADA! ---
    suspend fun getProductByCode(code: String): ProductEntity? {
        return productDao.findByCode(code)
    }

    suspend fun handleStockMovement(product: ProductEntity, type: MovimientoType, quantity: Int, user: String, refDoc: String?, reason: String?, date: Long) {
        val movimiento = MovimientoEntity(
            productId = product.id, type = type, quantity = quantity, user = user,
            referenceDocument = refDoc, reason = reason, createdAt = date
        )
        movimientoDao.insertMovimiento(movimiento)

        val newStock = if (type == MovimientoType.INGRESO) {
            product.stock + quantity
        } else {
            product.stock - quantity
        }.coerceAtLeast(0)
        val updatedProduct = product.copy(stock = newStock, lastUpdate = date)
        productDao.insertProduct(updatedProduct)

        try {
            val remoteMovement = RemoteMovement(
                productCode = product.code, quantity = quantity, type = type.name,
                referenceDocument = refDoc, reason = reason
            )
            movementApiService.createMovement(remoteMovement)
            Log.i("ProductRepository", "Movimiento de '${product.name}' sincronizado con el backend.")
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error al sincronizar movimiento con el backend", e)
        }
    }

    suspend fun getProductsReport(): List<ProductReport> {
        return inventoryApiService.getProductsReport()
    }
    
    suspend fun deleteProduct(product: ProductEntity) {
        updateRemoteStatus(product, "INACTIVE")
    }

    suspend fun restoreProduct(product: ProductEntity) {
        updateRemoteStatus(product, "ACTIVE")
    }

    suspend fun getInactiveProducts(): List<ProductEntity> {
        return try {
            val remoteProducts = inventoryApiService.getProductsByStatus("INACTIVE")
            remoteProducts.map { remote ->
                ProductEntity(
                    remoteId = remote.id, name = remote.nombre, code = remote.codigoQr,
                    stock = remote.stockActual, category = remote.categoria, zone = remote.ubicacion,
                    minStock = remote.minStock, status = "INACTIVE",
                    description = ""
                )
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error al obtener productos inactivos del backend.", e)
            emptyList()
        }
    }

    private suspend fun updateRemoteStatus(product: ProductEntity, newStatus: String) {
        product.remoteId?.let { remoteId ->
            try {
                val response = inventoryApiService.updateProductStatus(remoteId, newStatus)
                if (response.isSuccessful) {
                    if (newStatus == "INACTIVE") {
                        productDao.deleteProduct(product)
                    } else {
                        val updatedProduct = product.copy(status = newStatus)
                        productDao.insertProduct(updatedProduct)
                    }
                    Log.i("ProductRepository", "Producto '${product.name}' actualizado a $newStatus local y remotamente.")
                } else {
                    Log.e("ProductRepository", "Error del servidor al actualizar estado: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("ProductRepository", "Error de red al actualizar estado.", e)
            }
        } ?: Log.w("ProductRepository", "No se puede actualizar estado de '${product.name}', no tiene remoteId.")
    }
    
    suspend fun syncWithBackend() {
        try {
            Log.d("ProductRepository", "Iniciando sincronización con el backend...")
            val remoteProducts = inventoryApiService.getProductsByStatus("ACTIVE")
            val localProducts = productDao.getAllProducts().first()

            val remoteProductMap = remoteProducts.associateBy { it.id }
            val localProductMap = localProducts.associateBy { it.remoteId }

            for (localProduct in localProducts) {
                if (localProduct.remoteId != null && !remoteProductMap.containsKey(localProduct.remoteId)) {
                    productDao.deleteProduct(localProduct)
                    Log.d("ProductRepository", "Producto local '${localProduct.name}' eliminado (ya no existe en el servidor).")
                }
            }

            for (remoteProduct in remoteProducts) {
                val localEquivalent = localProductMap[remoteProduct.id]
                val entity = ProductEntity(
                    id = localEquivalent?.id ?: 0, 
                    remoteId = remoteProduct.id,
                    name = remoteProduct.nombre,
                    code = remoteProduct.codigoQr,
                    stock = remoteProduct.stockActual,
                    category = remoteProduct.categoria,
                    zone = remoteProduct.ubicacion,
                    minStock = remoteProduct.minStock,
                    status = "ACTIVE",
                    description = localEquivalent?.description ?: ""
                )
                productDao.insertProduct(entity)
            }
            Log.d("ProductRepository", "Sincronización completada. Total de productos remotos: ${remoteProducts.size}")
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error durante la sincronización con el backend.", e)
            throw e
        }
    }
}
