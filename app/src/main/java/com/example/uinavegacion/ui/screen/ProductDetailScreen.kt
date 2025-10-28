package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            modifier = Modifier.fillMaxSize().padding(16.dp),
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
