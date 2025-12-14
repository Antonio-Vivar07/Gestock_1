package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.remote.RemoteProductReport
import com.example.uinavegacion.viewmodel.ProductViewModel

private enum class StockLevel { BAJO, MEDIO, ALTO }

private fun getStockLevel(current: Int, min: Int): StockLevel {
    // Regla simple y estable:
    // - BAJO: stock <= min
    // - MEDIO: min < stock <= min*1.5
    // - ALTO: stock > min*1.5
    return when {
        current <= min -> StockLevel.BAJO
        current <= (min * 1.5).toInt() -> StockLevel.MEDIO
        else -> StockLevel.ALTO
    }
}

@Composable
fun ReportsDashboardScreen(productVm: ProductViewModel) {
    LaunchedEffect(Unit) { productVm.loadReport() }
    val report by productVm.report.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Reporte de Estado Actual",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
        }

        items(report) { item ->
            ReportCard(item)
        }
    }
}

@Composable
private fun ReportCard(item: RemoteProductReport) {
    val level = getStockLevel(item.currentStock, item.minStock)
    val levelColor = when (level) {
        StockLevel.BAJO -> Color(0xFFD32F2F)
        StockLevel.MEDIO -> Color(0xFFFFA000)
        StockLevel.ALTO -> Color(0xFF388E3C)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            val cat = item.categoria?.takeIf { it.isNotBlank() } ?: "sin categoría"
            val ub = item.ubicacion?.takeIf { it.isNotBlank() } ?: "sin ubicación"
            Text(
                text = "Categoría: $cat  |  Ubicación: $ub",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF616161)
            )

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Stock Actual: ${item.currentStock}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Nivel: ${level.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = levelColor
                )
            }

            Spacer(Modifier.height(6.dp))
            Text(
                text = "Última actualización: ${item.lastUpdate}",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF616161)
            )
        }
    }
}
