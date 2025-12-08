package com.example.uinavegacion.ui.screen

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
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.AppViewModelProvider
import com.example.uinavegacion.viewmodel.ProductViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEntryScreen(
    authVm: AuthViewModel,
    productVm: ProductViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Ingreso (Guía)", "Salida (Orden)")

    // --- ESTADOS PARA LA BÚSQUEDA ---
    var productQuery by remember { mutableStateOf("") }
    var productCode by remember { mutableStateOf("") } // Guarda el código del producto seleccionado
    var isSearchExpanded by remember { mutableStateOf(false) }
    val activeProducts by productVm.products.collectAsState() // Lista de productos ACTIVOS

    // --- ESTADOS PARA EL RESTO DEL FORMULARIO (SIN CAMBIOS) ---
    var quantity by remember { mutableStateOf("") }
    var referenceDoc by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    val formattedDate by remember(selectedDate) {
        derivedStateOf {
            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(selectedDate))
        }
    }
    var showDatePicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val session by authVm.session.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Pestañas (Sin Cambios)
        TabRow(selectedTabIndex = selectedTabIndex) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Selector de Fecha (Sin Cambios)
        OutlinedTextField(
            value = formattedDate,
            onValueChange = {},
            label = { Text("Fecha del Movimiento") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar Fecha")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    Button(onClick = {
                        selectedDate = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        showDatePicker = false
                    }) { Text("Aceptar") }
                },
                dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") } }
            ) { DatePicker(state = datePickerState) }
        }
        
        Spacer(Modifier.height(8.dp))

        // --- CAMPO DE BÚSQUEDA DE PRODUCTO (CORREGIDO CON AUTOCOMPLETADO) ---
        val filteredProducts = if (productQuery.isNotBlank()) {
            activeProducts.filter {
                it.name.contains(productQuery, ignoreCase = true) || it.code.contains(productQuery, ignoreCase = true)
            }
        } else {
            emptyList()
        }
        
        ExposedDropdownMenuBox(
            expanded = isSearchExpanded && filteredProducts.isNotEmpty(),
            onExpandedChange = { isSearchExpanded = !isSearchExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                value = productQuery,
                onValueChange = {
                    productQuery = it
                    isSearchExpanded = true
                },
                label = { Text("1. Buscar producto...") },
            )
            ExposedDropdownMenu(
                expanded = isSearchExpanded && filteredProducts.isNotEmpty(),
                onDismissRequest = { isSearchExpanded = false }
            ) {
                filteredProducts.forEach { product ->
                    DropdownMenuItem(
                        text = { Text("${product.name} (Código: ${product.code})") },
                        onClick = {
                            productCode = product.code // Guarda el código para el registro
                            productQuery = product.name // Muestra el nombre en el campo
                            isSearchExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(Modifier.height(8.dp))

        // Otros campos (Sin Cambios)
        val referenceLabel = if (selectedTabIndex == 0) "Nº Guía de Despacho" else "Nº Orden de Salida"
        OutlinedTextField(
            value = referenceDoc,
            onValueChange = { referenceDoc = it },
            label = { Text(referenceLabel) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("2. Cantidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.weight(1f)) 

        // Botón (Lógica sin cambios, ahora usa el 'productCode' correcto)
        Button(
            onClick = {
                val user = session?.username ?: "desconocido"
                val movementType = if (selectedTabIndex == 0) MovimientoType.INGRESO else MovimientoType.EGRESO
                scope.launch {
                    // La lógica ahora funciona porque 'productCode' se establece al seleccionar de la lista
                    val product = productVm.getProductByCode(productCode)
                    if (product != null) {
                        productVm.handleStockMovement(
                            product = product,
                            type = movementType,
                            quantity = quantity.toIntOrNull() ?: 0,
                            user = user,
                            refDoc = referenceDoc.ifBlank { null },
                            reason = null
                        )
                        snackbarHostState.showSnackbar("Movimiento registrado con éxito para ${product.name}")
                        // Limpiar campos
                        productQuery = ""
                        productCode = ""
                        quantity = ""
                        referenceDoc = ""
                    } else {
                        snackbarHostState.showSnackbar("Error: Producto no encontrado. Por favor, selecciónelo de la lista.")
                    }
                }
            },
            enabled = productCode.isNotBlank() && quantity.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text(if (selectedTabIndex == 0) "Registrar Entrada" else "Registrar Salida")
        }
        
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.CenterHorizontally))
    }
}
