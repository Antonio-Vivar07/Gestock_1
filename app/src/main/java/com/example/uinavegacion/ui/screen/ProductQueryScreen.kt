package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

// Data class local para esta pantalla para evitar conflictos
data class ConsultationProduct(val name: String, val category: String, val zone: String)

@Composable
fun ProductQueryScreen() {
    var showProductDialog by remember { mutableStateOf(false) }
    var selectedProduct by remember { mutableStateOf<ConsultationProduct?>(null) }

    // Productos ficticios y distintos para esta pantalla de consulta
    val queryProducts = listOf(
        ConsultationProduct("Impresora Multifuncional", "Tecnología", "Zona A"),
        ConsultationProduct("Jeans Clásicos", "Ropa y calzado", "Zona B"),
        ConsultationProduct("Café de Grano 1kg", "Alimentos", "Zona C"),
        ConsultationProduct("Caja de Herramientas", "Ferretería", "Zona A")
    )

    if (showProductDialog) {
        Dialog(onDismissRequest = { showProductDialog = false }) {
            Surface(modifier = Modifier.padding(16.dp), shape = MaterialTheme.shapes.medium) {
                LazyColumn {
                    items(queryProducts) { product ->
                        ListItem(
                            headlineContent = { Text(product.name) },
                            modifier = Modifier.clickable {
                                selectedProduct = product
                                showProductDialog = false
                            }
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = selectedProduct?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Buscar Producto") },
            trailingIcon = {
                IconButton(onClick = { showProductDialog = true }) {
                    Icon(Icons.Default.Search, contentDescription = "Abrir búsqueda")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        if (selectedProduct != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Información del Producto", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(16.dp))
                    InfoRow(label = "Categoría", value = selectedProduct!!.category)
                    InfoRow(label = "Zona", value = selectedProduct!!.zone)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold)
        Text(text = value)
    }
    Spacer(Modifier.height(8.dp))
}
