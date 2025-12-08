package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.viewmodel.ProductViewModel

@Composable
fun ProductQueryScreen(
    productVm: ProductViewModel,
    onProductFound: (Int) -> Unit,
    onGoToQrScanner: () -> Unit
) {
    // --- ¡CORRECCIÓN APLICADA! ---
    val products by productVm.products.collectAsState()
    
    var productCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = productCode,
            onValueChange = { productCode = it; errorMessage = null },
            label = { Text("Ingresa el código del producto") },
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    val foundProduct = products.find { it.code.equals(productCode, ignoreCase = true) }
                    if (foundProduct != null) {
                        onProductFound(foundProduct.id)
                    } else {
                        errorMessage = "Producto no encontrado"
                    }
                },
                enabled = productCode.isNotBlank()
            ) {
                Text("Buscar Producto")
            }
            Spacer(modifier = Modifier.width(16.dp))
            OutlinedButton(onClick = onGoToQrScanner) {
                Text("Escanear QR")
            }
        }
    }
}
