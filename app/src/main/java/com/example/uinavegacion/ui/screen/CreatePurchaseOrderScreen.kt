package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePurchaseOrderScreen(
    productId: String,
    onOrderGenerated: (String, String, Int) -> Unit
) {
    var quantity by remember { mutableStateOf("") }

    val providers = listOf("Proveedor A", "Proveedor B", "Proveedor C")
    var selectedProvider by remember { mutableStateOf(providers[0]) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val isFormValid = quantity.toIntOrNull() != null && quantity.toInt() > 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Generar Orden de Compra", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = "Producto (ID: $productId)",
            onValueChange = {},
            readOnly = true,
            label = { Text("Producto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = isDropdownExpanded,
            onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
        ) {
            OutlinedTextField(
                value = selectedProvider,
                onValueChange = {},
                readOnly = true,
                label = { Text("Proveedor") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isDropdownExpanded,
                onDismissRequest = { isDropdownExpanded = false }
            ) {
                providers.forEach { provider ->
                    DropdownMenuItem(
                        text = { Text(provider) },
                        onClick = {
                            selectedProvider = provider
                            isDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = quantity,
            onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
            label = { Text("Cantidad a pedir") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { onOrderGenerated(productId, selectedProvider, quantity.toInt()) },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Generar Orden")
        }
    }
}
