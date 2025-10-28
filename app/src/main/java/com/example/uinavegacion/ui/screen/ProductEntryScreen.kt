package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.data.local.movimiento.MovimientoType
import com.example.uinavegacion.data.local.product.ProductEntity
import com.example.uinavegacion.viewmodel.AppViewModelProvider
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class TransactionType { INGRESO, SALIDA }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEntryScreen(
    authVm: AuthViewModel,
    productVm: ProductViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val scope = rememberCoroutineScope()
    val session by authVm.session.collectAsState()
    val currentUser = session?.username ?: "Desconocido"

    // Estado de la UI
    var transactionType by remember { mutableStateOf(TransactionType.INGRESO) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var quantity by remember { mutableStateOf("") }
    var documentNumber by remember { mutableStateOf("") }
    var isProductDropdownExpanded by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Lógica de Fecha Personalizada
    var selectedDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    val allProducts by productVm.allProducts.collectAsState()

    val filteredProducts = if (searchQuery.isBlank()) emptyList()
    else allProducts.filter { it.name.contains(searchQuery, ignoreCase = true) || it.code.contains(searchQuery, ignoreCase = true) }

    val handleTransaction: () -> Unit = {
        scope.launch {
            val product = selectedProduct
            val qty = quantity.toIntOrNull()
            if (product != null && qty != null && qty > 0 && documentNumber.isNotBlank()) {
                 if (transactionType == TransactionType.SALIDA && product.stock < qty) {
                    snackbarHostState.showSnackbar("Error: Stock insuficiente para realizar la salida.")
                } else {
                    productVm.handleStockMovement(
                        product = product,
                        type = if (transactionType == TransactionType.INGRESO) MovimientoType.INGRESO else MovimientoType.EGRESO,
                        quantity = qty,
                        user = currentUser,
                        refDoc = documentNumber,
                        reason = if (transactionType == TransactionType.INGRESO) "Recepción de Guía" else "Despacho por Orden",
                        date = selectedDateMillis
                    )
                    snackbarHostState.showSnackbar("Movimiento registrado con éxito.")
                    // Resetear campos
                    searchQuery = ""; selectedProduct = null; quantity = ""; documentNumber = ""; selectedDateMillis = System.currentTimeMillis()
                }
            } else {
                snackbarHostState.showSnackbar("Error: Complete todos los campos requeridos.")
            }
        }
    }
    
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                Button(onClick = { 
                    selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    showDatePicker = false 
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
        Column(
            modifier = Modifier.fillMaxSize().padding(it).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TabRow(selectedTabIndex = transactionType.ordinal) {
                Tab(selected = transactionType == TransactionType.INGRESO, onClick = { transactionType = TransactionType.INGRESO }, text = { Text("Ingreso (Guía)") })
                Tab(selected = transactionType == TransactionType.SALIDA, onClick = { transactionType = TransactionType.SALIDA }, text = { Text("Salida (Orden)") })
            }

            // --- CAMPO DE FECHA RESTAURADO Y FUNCIONAL ---
            OutlinedTextField(
                value = dateFormatter.format(Date(selectedDateMillis)),
                onValueChange = {},
                readOnly = true,
                label = { Text("Fecha del Movimiento") },
                trailingIcon = { Icon(Icons.Default.DateRange, "Select Date") },
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true }
            )

            // --- FORMULARIO ANTERIOR RESTAURADO ---
            ExposedDropdownMenuBox(expanded = isProductDropdownExpanded && filteredProducts.isNotEmpty(), onExpandedChange = {}) {
                OutlinedTextField(
                    value = searchQuery, 
                    onValueChange = { searchQuery = it; isProductDropdownExpanded = true }, 
                    label = { Text("1. Buscar producto...") }, 
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isProductDropdownExpanded && filteredProducts.isNotEmpty(), 
                    onDismissRequest = { isProductDropdownExpanded = false }
                ) {
                    filteredProducts.forEach { product ->
                        DropdownMenuItem(
                            text = { Text("${product.name} (Stock: ${product.stock})") }, 
                            onClick = { 
                                selectedProduct = product
                                searchQuery = product.name
                                isProductDropdownExpanded = false 
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = documentNumber, 
                onValueChange = { documentNumber = it }, 
                label = { Text(if (transactionType == TransactionType.INGRESO) "N° Guía de Despacho" else "N° Orden de Compra") }, 
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quantity,
                onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it },
                label = { Text("2. Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedProduct != null
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = handleTransaction,
                enabled = selectedProduct != null && quantity.isNotBlank() && documentNumber.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (transactionType == TransactionType.INGRESO) "Registrar Entrada" else "Registrar Salida")
            }
        }
    }
}
