package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Data class para el estado del producto en el reporte
data class ProductStatus(val name: String, val currentStock: Int, val minStock: Int, val lastUpdate: String)

enum class StockLevel {
    BAJO, MEDIO, ALTO
}

fun getStockLevel(current: Int, min: Int): StockLevel {
    return when {
        current <= min -> StockLevel.BAJO
        current <= min * 1.5 -> StockLevel.MEDIO
        else -> StockLevel.ALTO
    }
}

@Composable
fun ReportsDashboardScreen() {

    // Lista de ejemplo de estados de productos
    val productStatuses = listOf(
        ProductStatus("Cable HDMI A", 5, 10, "18/10/2025 10:30"),
        ProductStatus("Laptop MSI Cyborg 15", 15, 10, "18/10/2025 09:15"),
        ProductStatus("Teclado Mecánico RGB", 30, 15, "17/10/2025 18:00"),
        ProductStatus("Mouse Inalámbrico", 8, 20, "16/10/2025 12:00")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Reporte de Estado Actual", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
        }
        items(productStatuses) { status ->
            ProductStatusCard(status = status)
        }
    }
}

@Composable
private fun ProductStatusCard(status: ProductStatus) {
    val level = getStockLevel(status.currentStock, status.minStock)
    val levelColor = when (level) {
        StockLevel.BAJO -> Color(0xFFD32F2F) // Rojo
        StockLevel.MEDIO -> Color(0xFFFFA000) // Ámbar
        StockLevel.ALTO -> Color(0xFF388E3C)  // Verde
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(status.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Stock Actual: ${status.currentStock}")
                Text("Nivel: ${level.name}", color = levelColor, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("Última actualización: ${status.lastUpdate}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
