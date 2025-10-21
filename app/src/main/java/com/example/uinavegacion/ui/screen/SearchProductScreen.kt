package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// ESTA PANTALLA ES AHORA EXCLUSIVAMENTE PARA EL FLUJO DEL BOTÓN 'STOCK'

@Composable
fun SearchProductScreen(
    onProductClick: (String) -> Unit // La acción para navegar al detalle del stock
) {
    var searchQuery by remember { mutableStateOf("") }

    val stockProducts = listOf(
        Product("SKU-001", "Laptop MSI Cyborg 15", "...", 12, "Tecnología", "Zona A"),
        Product("SKU-002", "Cable HDMI A", "...", 58, "Tecnología", "Zona B"),
        Product("SKU-003", "Teclado Mecánico RGB", "...", 34, "Tecnología", "Zona A"),
        Product("SKU-004", "Martillo de bola", "...", 112, "Ferretería", "Zona C")
    )

    val filteredProducts = if (searchQuery.isBlank()) {
        stockProducts
    } else {
        stockProducts.filter {
            it.name.contains(searchQuery, ignoreCase = true) || it.id.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Buscar por nombre o SKU...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredProducts) { product ->
                ListItem(
                    headlineContent = { Text(product.name) },
                    supportingContent = { Text("ID: ${product.id}") },
                    trailingContent = { Text("Stock: ${product.stock}") },
                    modifier = Modifier.clickable { onProductClick(product.id) }
                )
                Divider()
            }
        }
    }
}