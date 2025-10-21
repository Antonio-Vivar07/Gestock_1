package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole

@Composable
fun ProductMenuScreen(
    onGoRegisterProduct: (() -> Unit)? = null
) {
    val authVm: AuthViewModel = viewModel()
    val user = authVm.session
    // --- LÍNEA CORREGIDA ---
    val userLabel = if (user?.role == UserRole.TRABAJADOR) "Trabajador" else "Usuario"

    val bg = Color(0xFFE7F2FF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = "User : $userLabel",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Spacer(Modifier.height(12.dp))

            // --- TÍTULO CAMBIADO ---
            Text(
                text = "Producto nuevo",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(Modifier.height(24.dp))

            // --- BOTÓN ÚNICO ---
            OutlinedButton(
                onClick = { onGoRegisterProduct?.invoke() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear producto")
            }
        }
    }
}
