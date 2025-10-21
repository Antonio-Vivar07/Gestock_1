package com.example.uinavegacion.ui.screen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter

// --- IMPORTACIONES CORREGIDAS ---
import com.example.uinavegacion.ui.screen.Product
import com.example.uinavegacion.ui.screen.ProductEntryViewModel
import com.google.zxing.common.BitMatrix

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEntryScreen() {
    
    val formVm: ProductEntryViewModel = viewModel()
    var showQrDialog by remember { mutableStateOf(false) }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    var searchQuery by remember { mutableStateOf("") }
    var isSearchDropdownExpanded by remember { mutableStateOf(false) }
    
    val allProducts = listOf(
        Product("SKU-001", "Laptop MSI Cyborg 15", "Laptop gamer con i7 y RTX 4050", 12, "Tecnología", "Zona A"),
        Product("SKU-002", "Cable HDMI A", "Cable de 1 metro, 1080p Full HD", 58, "Tecnología", "Zona B"),
        Product("SKU-003", "Teclado Mecánico RGB", "Teclado con switches azules y RGB", 34, "Tecnología", "Zona A")
    )

    val filteredProducts = allProducts.filter {
        it.name.contains(searchQuery, ignoreCase = true) && searchQuery.isNotBlank()
    }

    var selectedProvider by remember { mutableStateOf("Proveedor Nacional") }
    var isProviderDropdownExpanded by remember { mutableStateOf(false) }
    var selectedWarehouse by remember { mutableStateOf("Bodega A / Quilicura") }
    var isWarehouseDropdownExpanded by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf("") }

    if (showQrDialog && qrCodeBitmap != null) {
        AlertDialog(
            onDismissRequest = { showQrDialog = false },
            title = { Text("Código QR Generado") },
            text = { Image(bitmap = qrCodeBitmap!!.asImageBitmap(), contentDescription = "Código QR", modifier = Modifier.size(256.dp)) },
            confirmButton = { Button(onClick = { showQrDialog = false }) { Text("Cerrar") } }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Ingreso a bodega", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(32.dp))

        ExposedDropdownMenuBox(
            expanded = isSearchDropdownExpanded && filteredProducts.isNotEmpty(),
            onExpandedChange = { isSearchDropdownExpanded = !isSearchDropdownExpanded }
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it; formVm.selectedProduct = null; isSearchDropdownExpanded = true },
                label = { Text("Nombre de producto") },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isSearchDropdownExpanded && filteredProducts.isNotEmpty(),
                onDismissRequest = { isSearchDropdownExpanded = false }
            ) {
                filteredProducts.forEach { product ->
                    DropdownMenuItem(
                        text = { Text(product.name) },
                        onClick = { formVm.selectedProduct = product; searchQuery = product.name; isSearchDropdownExpanded = false }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = formVm.selectedProduct?.id ?: "", onValueChange = {}, readOnly = true, label = { Text("Codigo producto") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = formVm.selectedProduct?.description ?: "", onValueChange = {}, readOnly = true, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = isProviderDropdownExpanded,
            onExpandedChange = { isProviderDropdownExpanded = !isProviderDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedProvider,
                onValueChange = {},
                readOnly = true,
                label = { Text("Proveedor") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isProviderDropdownExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isProviderDropdownExpanded,
                onDismissRequest = { isProviderDropdownExpanded = false }
            ) {
                listOf("Proveedor Nacional", "Importadora XYZ", "Insumos Rápidos").forEach { provider ->
                    DropdownMenuItem(text = { Text(provider) }, onClick = { selectedProvider = provider; isProviderDropdownExpanded = false })
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        ExposedDropdownMenuBox(
            expanded = isWarehouseDropdownExpanded,
            onExpandedChange = { isWarehouseDropdownExpanded = !isWarehouseDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedWarehouse,
                onValueChange = {},
                readOnly = true,
                label = { Text("Asignar bodega") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isWarehouseDropdownExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isWarehouseDropdownExpanded,
                onDismissRequest = { isWarehouseDropdownExpanded = false }
            ) {
                listOf("Bodega A / Quilicura", "Bodega B / Maipú").forEach { bodega ->
                    DropdownMenuItem(text = { Text(bodega) }, onClick = { selectedWarehouse = bodega; isWarehouseDropdownExpanded = false })
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedTextField(value = quantity, onValueChange = { if (it.all { char -> char.isDigit() }) quantity = it }, label = { Text("Cantidad") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.weight(1f))

        Button(
            onClick = {
                val product = formVm.selectedProduct
                if (product != null && quantity.isNotBlank()) {
                    val qrData = """
                    ID Producto: ${product.id}
                    Nombre: ${product.name}
                    Descripción: ${product.description}
                    Proveedor: $selectedProvider
                    Bodega: $selectedWarehouse
                    Cantidad: $quantity
                    """.trimIndent()
                    qrCodeBitmap = generateQrCode(qrData)
                    showQrDialog = true
                }
            },
            enabled = formVm.selectedProduct != null && quantity.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Imprime etiqueta")
        }
    }
}

private fun generateQrCode(text: String): Bitmap {
    val size = 512
    val hints = hashMapOf<EncodeHintType, Any>()
    hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
    val bitMatrix: BitMatrix = QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}
