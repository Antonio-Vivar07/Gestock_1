package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.ProductViewModel

@Composable
fun ProductDetailScreen(
    productId: Int?,
    productCode: String?,
    authVm: AuthViewModel,
    productVm: ProductViewModel,
    onBack: () -> Unit, // Para la flecha de la TopAppBar
    onGoToInventoryControl: () -> Unit // Para el nuevo botón
) {
    val productState by productVm.allProducts.collectAsState()

    val product = remember(productState, productId, productCode) {
        if (productId != null) {
            productState.find { it.id == productId }
        } else if (productCode != null) {
            productState.find { it.code == productCode }
        } else {
            null
        }
    }

    if (product == null) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Información Principal
            Text(product.name, style = MaterialTheme.typography.headlineLarge)
            Text("Código: ${product.code}", style = MaterialTheme.typography.titleMedium)

            Divider(modifier = Modifier.padding(vertical = 24.dp))

            // Ficha de Datos
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoRow(label = "Stock Actual:", value = product.stock.toString())
                InfoRow(label = "Ubicación (Zona):", value = product.zone)
                InfoRow(label = "Stock Mínimo:", value = product.minStock.toString())
            }

            // ✅ GRÁFICO tipo área (como tu ejemplo) usando SOLO stock actual
            // Como tu modelo NO trae stock máximo/inicial,
            // usamos una referencia dinámica por producto: stockMaximoRef = minStock * 2
            // La mitad queda en minStock, y el color cambia según:
            // BAJO < mitad, MEDIO == mitad, ALTO > mitad
            val stockMaximoRef = remember(product) {
                (product.minStock * 2).coerceAtLeast(1)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Nivel de stock", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            StockAreaChart(
                stockActual = product.stock,
                stockMaximo = stockMaximoRef,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.weight(1f)) // Empuja el botón hacia abajo

            Button(
                onClick = onGoToInventoryControl,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a Control de Inventario")
            }
        }
    }
}

// Componente auxiliar para mostrar la información de forma limpia
@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(text = value, fontSize = 18.sp)
    }
}

/* =========================
   ✅ GRÁFICO (sin conflictos)
   ========================= */

private enum class StockLevelChart { BAJO, MEDIO, ALTO }

private fun stockLevelChart(stockActual: Int, stockMaximo: Int): StockLevelChart {
    if (stockMaximo <= 0) return StockLevelChart.BAJO
    val mitad = stockMaximo / 2
    return when {
        stockActual < mitad -> StockLevelChart.BAJO
        stockActual == mitad -> StockLevelChart.MEDIO
        else -> StockLevelChart.ALTO
    }
}

private fun stockColorChart(level: StockLevelChart): Color {
    return when (level) {
        StockLevelChart.BAJO -> Color(0xFFD32F2F)   // rojo
        StockLevelChart.MEDIO -> Color(0xFFFFC107)  // amarillo
        StockLevelChart.ALTO -> Color(0xFF2E7D32)   // verde
    }
}

@Composable
private fun StockAreaChart(
    stockActual: Int,
    stockMaximo: Int,
    modifier: Modifier = Modifier
) {
    val level = stockLevelChart(stockActual, stockMaximo)
    val color = stockColorChart(level)

    Column(modifier = modifier) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Stock actual: $stockActual", style = MaterialTheme.typography.bodyMedium)
            Text(
                text = when (level) {
                    StockLevelChart.BAJO -> "BAJO"
                    StockLevelChart.MEDIO -> "MEDIO"
                    StockLevelChart.ALTO -> "ALTO"
                },
                color = color,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val width = size.width
            val height = size.height

            val ratio = (stockActual.toFloat() / stockMaximo.toFloat()).coerceIn(0f, 1f)
            val yLine = height * (1f - ratio)

            // Área rellena
            val areaPath = Path().apply {
                moveTo(0f, height)
                lineTo(0f, yLine)
                lineTo(width, yLine)
                lineTo(width, height)
                close()
            }

            drawPath(path = areaPath, color = color.copy(alpha = 0.35f))

            // Línea superior
            drawLine(
                color = color,
                start = Offset(0f, yLine),
                end = Offset(width, yLine),
                strokeWidth = 6f
            )

            // Línea base
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, height),
                end = Offset(width, height),
                strokeWidth = 2f
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(Color(0xFFD32F2F), "Bajo")
            LegendItem(Color(0xFFFFC107), "Medio")
            LegendItem(Color(0xFF2E7D32), "Alto")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(10.dp)) {
            drawRect(color = color, size = Size(size.width, size.height))
        }
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}
