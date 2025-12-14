package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
<<<<<<< Updated upstream
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
=======
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
>>>>>>> Stashed changes
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
<<<<<<< Updated upstream
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
    val umbralMedio = min * 0.5
    return when {
        current > min -> StockLevel.ALTO
        current >= umbralMedio -> StockLevel.MEDIO
        else -> StockLevel.BAJO
=======
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
>>>>>>> Stashed changes
    }
}

@Composable
<<<<<<< Updated upstream
fun ReportsDashboardScreen(
    reportsViewModel: ReportsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val productReports by reportsViewModel.productReports.collectAsState()
    val isLoading by reportsViewModel.isLoading.collectAsState()
    val errorMessage by reportsViewModel.errorMessage.collectAsState()

    var showUserInfoDialog by remember { mutableStateOf(false) }
    var userInfoToShow by remember { mutableStateOf<String?>(null) }

    if (showUserInfoDialog) {
        UserInfoDialog(
            userInfo = userInfoToShow,
            onDismiss = { showUserInfoDialog = false }
        )
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when {
            isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            errorMessage != null -> {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // --- ¡CORRECCIÓN! Se maneja el estado de lista vacía ---
            productReports.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay productos activos para mostrar en el reporte.",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("Reporte de Estado Actual", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                    }
                    items(productReports) { report ->
                        ProductStatusCard(
                            report = report,
                            onShowUser = {
                                userInfoToShow = report.lastModifiedBy
                                showUserInfoDialog = true
                            }
                        )
                    }
                }
            }
=======
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
>>>>>>> Stashed changes
        }
    }
}

@Composable
<<<<<<< Updated upstream
private fun ProductStatusCard(report: ProductReport, onShowUser: () -> Unit) {
    val level = getStockLevel(report.currentStock, report.minStock)
=======
private fun ReportCard(item: RemoteProductReport) {
    val level = getStockLevel(item.currentStock, item.minStock)
>>>>>>> Stashed changes
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
<<<<<<< Updated upstream
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(report.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "Categoría: ${report.categoria ?: "N/A"}  |  Ubicación: ${report.ubicacion ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                Text("Stock Actual: ${report.currentStock}")
                Spacer(Modifier.height(4.dp))
                Text("Última actualización: ${report.lastUpdate}", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("Nivel: ${level.name}", color = levelColor, fontWeight = FontWeight.Bold)
                IconButton(onClick = onShowUser) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Ver último usuario",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
=======
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
>>>>>>> Stashed changes
        }
    }
}

@Composable
private fun UserInfoDialog(userInfo: String?, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Última Modificación") },
        text = { Text(userInfo ?: "No hay información de usuario disponible.") },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
