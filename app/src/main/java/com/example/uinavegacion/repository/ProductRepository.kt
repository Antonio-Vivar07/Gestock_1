package com.example.uinavegacion.repository

import android.util.Log
import com.example.uinavegacion.data.local.movimiento.MovimientoDao
import com.example.uinavegacion.data.local.movimiento.MovimientoEntity
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductDao
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.data.remote.InventoryApiService
import com.example.uinavegacion.data.remote.MovementApiService
<<<<<<< Updated upstream
import com.example.uinavegacion.data.remote.ProductReport
=======
import com.example.uinavegacion.data.remote.ProductApiService
import com.example.uinavegacion.data.remote.ReportApiService
>>>>>>> Stashed changes
import com.example.uinavegacion.data.remote.RemoteMovement
import com.example.uinavegacion.data.remote.RemoteProduct
import com.example.uinavegacion.data.remote.RemoteProductFull
import com.example.uinavegacion.data.remote.RemoteProductReport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ProductRepository(
    private val productDao: ProductDao,
    private val movimientoDao: MovimientoDao,
<<<<<<< Updated upstream
    private val inventoryApiService: InventoryApiService,
    private val movementApiService: MovementApiService
=======
    private val inventoryApi: InventoryApiService,
    private val movementApi: MovementApiService,
    private val productApi: ProductApiService,
    private val reportApi: ReportApiService
>>>>>>> Stashed changes
) {

    fun getAllProducts(): Flow<List<ProductEntity>> {
        return productDao.getAllProducts()
    }

<<<<<<< Updated upstream
    suspend fun createProduct(name: String, code: String, description: String, category: String, zone: String, minStock: Int) {
        val newProduct = ProductEntity(
            name = name, code = code, description = description, category = category, zone = zone,
            minStock = minStock, stock = 0, lastUpdate = System.currentTimeMillis()
=======
    suspend fun refreshProductsFromBackend() {
        val remote = productApi.getActiveProducts()
        productDao.clearAll()
        remote.forEach { p ->
            productDao.insertProduct(p.toEntity())
        }
    }

    suspend fun refreshDeletedProductsFromBackend(): List<ProductEntity> {
        val remote = productApi.getProductsByStatus("DELETED")
        return remote.map { it.toEntity() }
    }

    suspend fun deleteRemoteProduct(remoteId: String): RemoteProductFull {
        val deleted = productApi.deleteProduct(remoteId)
        // sincroniza DB local (el listado activo ya no lo traerá)
        productDao.deleteByRemoteId(remoteId)
        return deleted
    }

    suspend fun restoreRemoteProduct(remoteId: String): RemoteProductFull {
        val restored = productApi.restoreProduct(remoteId)
        // vuelve a insertarlo en local
        productDao.insertProduct(restored.toEntity())
        return restored
    }

    suspend fun getReport(): List<RemoteProductReport> = reportApi.getReport()

    suspend fun addProduct(product: ProductEntity) {
        // 1) Guardar local primero
        val localId = productDao.insertProductAndGetId(product)

        // 2) Enviar al backend (MongoDB)
        val remoteProduct = RemoteProduct(
            id = null,
            nombre = product.name,
            codigoQr = product.code,
            stockActual = product.stock,
            categoria = product.category,
            ubicacion = product.zone
>>>>>>> Stashed changes
        )
        productDao.insertProduct(newProduct)

        try {
<<<<<<< Updated upstream
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
=======
            val created = inventoryApi.createProduct(remoteProduct)
            // 3) Actualizar el registro local con el remoteId si el backend lo devolvió
            if (!created.id.isNullOrBlank()) {
                productDao.updateRemoteId(localId.toInt(), created.id!!)
>>>>>>> Stashed changes
            }
        } catch (e: Exception) {
            Log.e("ProductRepository", "Error al sincronizar nuevo producto con el backend", e)
        }
    }
<<<<<<< Updated upstream
    
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
=======

    suspend fun addMovement(
        product: ProductEntity,
        quantity: Int,
        type: MovimientoType,
        refDoc: String?,
        reason: String?,
        user: String,
        createdAtMillis: Long = System.currentTimeMillis()
    ) {
        // Guardar local (Room) para tener historial aun si falla red
        val movement = MovimientoEntity(
            productId = product.id,
            type = type,
            quantity = quantity,
            reason = reason,
            referenceDocument = refDoc,
            user = user,
            createdAt = createdAtMillis
        )
        movimientoDao.insertMovimiento(movement)

        // Enviar al backend (el backend toma el user desde el JWT, y genera createdAt si viene null)
        val remoteMovement = RemoteMovement(
            productCode = product.code,
            quantity = quantity,
            type = type.name,
            referenceDocument = refDoc,
            reason = reason,
            createdAt = null
        )
>>>>>>> Stashed changes

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


    private fun RemoteProductFull.toEntity(): ProductEntity {
        return ProductEntity(
            id = 0,
            remoteId = this.id,
            name = this.nombre,
            code = this.codigoQr,
            description = "",
            category = this.categoria ?: "",
            zone = this.ubicacion ?: "",
            minStock = this.minStock ?: 0,
            stock = this.stockActual ?: 0
        )
    }
}
