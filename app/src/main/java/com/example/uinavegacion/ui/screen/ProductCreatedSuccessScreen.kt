package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.ui.components.QrCodeBox

@Composable
fun ProductCreatedSuccessScreen(
    productName: String,
    productCode: String, // <-- Parámetro SKU para el QR
    onGoToInventoryControl: () -> Unit,
    onAddAnotherProduct: () -> Unit
) {
    var showQr by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = "Éxito",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(100.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("¡Producto Creado!", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "'$productName' ha sido registrado en el sistema con el SKU: $productCode",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(32.dp))

        if (showQr) {
            // Muestra el QR Code usando el componente que ya teníamos
            QrCodeBox(data = productCode, modifier = Modifier.fillMaxWidth(0.8f))
            Spacer(Modifier.height(16.dp))
            Button(onClick = { /* Lógica futura para imprimir/guardar */ }) {
                Text("Imprimir / Guardar Etiqueta")
            }
        } else {
            // El botón que me has pedido 10 veces
            Button(onClick = { showQr = true }) {
                Text("Generar Código QR")
            }
        }

        Spacer(Modifier.weight(1f))

        TextButton(onClick = onAddAnotherProduct) {
            Text("Añadir otro producto")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onGoToInventoryControl, modifier = Modifier.fillMaxWidth()) {
            Text("Volver al menú principal")
        }
    }
}