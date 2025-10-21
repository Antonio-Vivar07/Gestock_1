package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole

@Composable
fun CreateProductScreen(onCreateProduct: (String) -> Unit) {
    val authVm: AuthViewModel = viewModel()
    val user = authVm.session
    // --- LÍNEA CORREGIDA ---
    val userLabel = if (user?.role == UserRole.TRABAJADOR) "Trabajador" else "Usuario"

    var productName by remember { mutableStateOf("") }
    var productCode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado (opcional, basado en el diseño)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            Text(text = "User : $userLabel", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(16.dp))

        Text("Crear producto nuevo", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Nombre de producto") },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = productCode,
            onValueChange = { productCode = it },
            label = { Text("Codigo producto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f)) // Empuja el botón hacia abajo

        Button(
            onClick = { onCreateProduct(productName) },
            enabled = productName.isNotBlank() && productCode.isNotBlank() && description.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear producto nuevo")
        }
    }
}
