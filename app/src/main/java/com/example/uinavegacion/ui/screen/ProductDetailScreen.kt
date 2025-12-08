package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductDetailScreen(
    productId: Int?,
    productCode: String?,
    authVm: AuthViewModel,
    productVm: ProductViewModel,
    onBack: () -> Unit,
    onGoToInventoryControl: () -> Unit
) {
    val productState by productVm.products.collectAsState()
    val session by authVm.session.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showMovementDialog by remember { mutableStateOf(false) }
    var movementType by remember { mutableStateOf(MovimientoType.INGRESO) }

    val product = remember(productState, productId, productCode) {
        if (productId != null) {
            productState.find { it.id == productId }
        } else if (productCode != null) {
            productState.find { it.code == productCode }
        } else {
            null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (product == null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                CircularProgressIndicator()
            }
        } else {
            // --- DIÁLOGO PARA REGISTRAR MOVIMIENTO ---
            if (showMovementDialog) {
                MovementInputDialog(
                    productName = product.name,
                    movementType = movementType,
                    onDismiss = { showMovementDialog = false },
                    onConfirm = { quantity ->
                        val user = session?.username ?: "desconocido"
                        productVm.handleStockMovement(product, movementType, quantity, user, null, null)
                        showMovementDialog = false
                        scope.launch {
                            snackbarHostState.showSnackbar("Movimiento registrado con éxito.")
                        }
                    }
                )
            }

            // --- CONTENIDO DE LA PANTALLA ---
            Column(
                modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
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

                Spacer(Modifier.weight(1f))

                // --- BOTONES DE ACCIÓN RESTAURADOS ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            movementType = MovimientoType.INGRESO
                            showMovementDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Registrar Ingreso")
                    }
                    OutlinedButton(
                        onClick = {
                            movementType = MovimientoType.EGRESO
                            showMovementDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Registrar Salida")
                    }
                }
            }
        }
    }
}

// --- DIÁLOGO COMPONIBLE (NUEVO) ---
@Composable
private fun MovementInputDialog(
    productName: String,
    movementType: MovimientoType,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    val title = if (movementType == MovimientoType.INGRESO) "Registrar Ingreso" else "Registrar Salida"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                Text("Producto: $productName")
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it.filter { char -> char.isDigit() } },
                    label = { Text("Cantidad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(quantity.toIntOrNull() ?: 0) },
                enabled = quantity.isNotBlank() && quantity.toIntOrNull() ?: 0 > 0
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Componente auxiliar para mostrar la información (SIN CAMBIOS)
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
