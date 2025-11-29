package com.example.uinavegacion.repository

import com.example.uinavegacion.data.local.movimiento.MovimientoDao
import com.example.uinavegacion.data.local.movimiento.MovimientoEntity
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductDao
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.data.remote.InventoryApiService
import com.example.uinavegacion.data.remote.MovementApiService
import com.example.uinavegacion.data.remote.RemoteMovement
import com.example.uinavegacion.data.remote.RemoteProduct
import kotlinx.coroutines.flow.Flow

class ProductRepository(
    private val productDao: ProductDao,
    private val movimientoDao: MovimientoDao,
    private val inventoryApi: InventoryApiService,
    private val movementApi: MovementApiService    // 👈 NUEVO: servicio remoto de movimientos
) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllProducts()

    suspend fun getProductByCode(code: String): ProductEntity? = productDao.findByCode(code)

    suspend fun createProduct(
        name: String,
        code: String,
        description: String,
        category: String,
        zone: String,
        minStock: Int
    ) {
        // 1) Guardar local en Room
        val productEntity = ProductEntity(
            name = name,
            code = code,
            description = description,
            category = category,
            zone = zone,
            minStock = minStock
        )
        productDao.insertProduct(productEntity)

        // 2) Enviar al backend (MongoDB -> products)
        val remoteProduct = RemoteProduct(
            nombre = name,
            codigoQr = code,
            stockActual = 0,
            categoria = category,
            ubicacion = zone
        )

        try {
            inventoryApi.createProduct(remoteProduct)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteProduct(product: ProductEntity) {
        productDao.deleteProduct(product)
    }

    suspend fun handleStockMovement(
        product: ProductEntity,
        type: MovimientoType,
        quantity: Int,
        user: String,
        refDoc: String?,
        reason: String?,
        date: Long
    ) {
        // 1) Calcular delta y nuevo stock
        val (delta, newStock) = when (type) {

            MovimientoType.INGRESO -> {
                val d = quantity
                val ns = product.stock + quantity
                d to ns
            }

            MovimientoType.EGRESO -> {
                val d = -quantity
                val ns = (product.stock - quantity).coerceAtLeast(0)
                d to ns
            }

            MovimientoType.AJUSTE -> {
                val d = quantity - product.stock
                val ns = quantity
                d to ns
            }

            else -> {
                0 to product.stock
            }
        }

        // 2) Guardar movimiento en Room (local)
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

        // 3) Enviar movimiento al backend (MongoDB -> movements)
        val remoteMovement = RemoteMovement(
            productCode = product.code,   // mapea con Product.codigoQr en backend
            quantity = quantity,
            type = type.name,             // "INGRESO" / "EGRESO" / "AJUSTE"
            referenceDocument = refDoc,
            reason = reason,
            createdAt = null              // backend genera "dd-MM-yyyy HH:mm:ss"
        )

        try {
            movementApi.createMovement(remoteMovement)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
