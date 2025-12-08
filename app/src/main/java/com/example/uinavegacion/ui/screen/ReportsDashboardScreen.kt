package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.data.remote.ProductReport
import com.example.uinavegacion.viewmodel.AppViewModelProvider
import com.example.uinavegacion.viewmodel.ReportsViewModel

enum class StockLevel {
    BAJO, MEDIO, ALTO
}

fun getStockLevel(current: Int, min: Int): StockLevel {
    if (min <= 0) {
        return if (current > 0) StockLevel.ALTO else StockLevel.BAJO
    }

    // Lógica final con tu regla del 50%
    val umbralMedio = min * 0.5

    return when {
        // Si es mayor que el mínimo, es ALTO.
        current > min -> StockLevel.ALTO
        
        // Si es mayor O IGUAL al 50%, es MEDIO.
        current >= umbralMedio -> StockLevel.MEDIO

        // Si es menor que el 50%, es BAJO.
        else -> StockLevel.BAJO
    }
}

@Composable
fun ReportsDashboardScreen(
    reportsViewModel: ReportsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val productReports by reportsViewModel.productReports.collectAsState()
    val isLoading by reportsViewModel.isLoading.collectAsState()
    val errorMessage by reportsViewModel.errorMessage.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
            else -> {
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
                    items(productReports) { report ->
                        ProductStatusCard(report = report)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductStatusCard(report: ProductReport) {
    val level = getStockLevel(report.currentStock, report.minStock)
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
            Text(report.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Stock Actual: ${report.currentStock}")
                Text("Nivel: ${level.name}", color = levelColor, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            Text("Última actualización: ${report.lastUpdate}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
