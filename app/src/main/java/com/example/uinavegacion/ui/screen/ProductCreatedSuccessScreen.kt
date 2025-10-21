package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
// --- IMPORTACIÓN AÑADIDA ---
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ProductCreatedSuccessScreen(
    productName: String,
    onGoToInventoryControl: () -> Unit,
    onAddAnotherProduct: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Producto creado exitosamente",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF006400) // Verde oscuro
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = productName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Nombre de producto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        // --- DOS BOTONES ---
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = onGoToInventoryControl, modifier = Modifier.fillMaxWidth()) {
                Text("Ir a Control de Inventario")
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = onAddAnotherProduct, modifier = Modifier.fillMaxWidth()) {
                Text("Agregar Otro Producto")
            }
        }
    }
}
