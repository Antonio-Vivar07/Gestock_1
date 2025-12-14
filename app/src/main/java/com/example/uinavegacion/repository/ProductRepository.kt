package com.example.uinavegacion.repository

import com.example.uinavegacion.data.local.movimiento.MovimientoDao
import com.example.uinavegacion.data.local.movimiento.MovimientoEntity
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductDao
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.data.remote.InventoryApiService
import com.example.uinavegacion.data.remote.MovementApiService
import com.example.uinavegacion.data.remote.ProductApiService
import com.example.uinavegacion.data.remote.ReportApiService
import com.example.uinavegacion.data.remote.RemoteMovement
import com.example.uinavegacion.data.remote.RemoteProduct
import com.example.uinavegacion.data.remote.RemoteProductFull
import com.example.uinavegacion.data.remote.RemoteProductReport
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val movimientoDao: MovimientoDao,
    private val inventoryApi: InventoryApiService,
    private val movementApi: MovementApiService,
    private val productApi: ProductApiService,
    private val reportApi: ReportApiService
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

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
            ubicacion = product.zone,
            minStock = product.minStock // ✅ FIX: ahora se envía minStock
        )

        try {
            val created = inventoryApi.createProduct(remoteProduct)
            // 3) Actualizar el registro local con el remoteId si el backend lo devolvió
            if (!created.id.isNullOrBlank()) {
                productDao.updateRemoteId(localId.toInt(), created.id!!)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

        try {
            movementApi.createMovement(remoteMovement)
        } catch (e: Exception) {
            e.printStackTrace()
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
