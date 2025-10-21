package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole

@Composable
fun StockScreen(
    productId: String,
    onGenerateOrder: () -> Unit
) {
    val authVm: AuthViewModel = viewModel()
    val user = authVm.session
    val userLabel = if (user?.role == UserRole.TRABAJADOR) "Trabajador" else "Usuario"

    var productName by remember { mutableStateOf("Producto (ID: $productId)") }
    var showDialog by remember { mutableStateOf(false) }
    var dialogAction by remember { mutableStateOf("") }

    var currentStock by remember { mutableStateOf(24) }

    val bgColor = Color(0xFFE7F2FF)
    val buttonColor = Color(0xFFE0D9FF)

    if (showDialog) {
        StockMovementDialog(
            action = dialogAction,
            onDismiss = { showDialog = false },
            onConfirm = { quantity ->
                if (dialogAction == "Entrada") {
                    currentStock += quantity
                } else {
                    currentStock -= quantity
                }
                showDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "User : $userLabel", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
        Spacer(Modifier.height(8.dp))
        Text(text = "Stock", style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold))

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = productName, onValueChange = {},
            label = { Text("Producto") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true
        )

        Spacer(Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { dialogAction = "Entrada"; showDialog = true }, modifier = Modifier.weight(1f)) {
                Text("Entrada de Stock")
            }
            Button(onClick = { dialogAction = "Salida"; showDialog = true }, modifier = Modifier.weight(1f)) {
                Text("Salida de Stock")
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Inventario stock", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            StockIndicator(label = "Stock", value = "$currentStock units")
            StockIndicator(label = "Min. Stock", value = "10 units")
            StockIndicator(label = "Max. Stock", value = "30 units")
        }

        Spacer(Modifier.weight(1f))

        StockButton(
            text = "Generar orden de compra", 
            modifier = Modifier.fillMaxWidth(), 
            buttonColor = buttonColor,
            onClick = onGenerateOrder
        )
    }
}

@Composable
private fun StockMovementDialog(action: String, onDismiss: () -> Unit, onConfirm: (Int) -> Unit) {
    var quantity by remember { mutableStateOf("") }
    val isQuantityValid = quantity.toIntOrNull() != null && quantity.toInt() > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("$action de Stock") },
        text = {
            Column {
                Text("¿Qué cantidad deseas ${if (action == "Entrada") "añadir" else "quitar"}?")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(quantity.toInt()) },
                enabled = isQuantityValid
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun StockIndicator(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp))
    }
}

@Composable
private fun StockButton(text: String, modifier: Modifier = Modifier, buttonColor: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
    }
}
