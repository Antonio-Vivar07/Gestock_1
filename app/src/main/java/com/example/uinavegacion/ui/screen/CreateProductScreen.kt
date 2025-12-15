package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.viewmodel.AppViewModelProvider
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.ProductViewModel

@Composable
fun CreateProductScreen(
    authVm: AuthViewModel,
    productVm: ProductViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onCreateProduct: (String, String) -> Unit // <-- FIRMA ACTUALIZADA
) {
    var productName by remember { mutableStateOf("") }
    var productCode by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var zone by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Ingresar Nuevo Producto", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(value = productName, onValueChange = { productName = it }, label = { Text("Nombre de producto") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = productCode, onValueChange = { productCode = it }, label = { Text("Código / SKU") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción (Opcional)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Categoría") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = zone, onValueChange = { zone = it }, label = { Text("Zona o Ubicación") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
            value = minStock,
            onValueChange = { if (it.all { char -> char.isDigit() }) minStock = it },
            label = { Text("Stock Mínimo (Alerta)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = { 
                productVm.createProduct(productName, productCode, description, category, zone, minStock.toIntOrNull() ?: 0)
                // --- LLAMADA ACTUALIZADA ---
                onCreateProduct(productName, productCode)
            },
            enabled = productName.isNotBlank() && productCode.isNotBlank() && category.isNotBlank() && zone.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Producto")
        }
    }
}
