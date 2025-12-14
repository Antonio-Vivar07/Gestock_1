package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole

@Composable
fun RegisterScreen(
    authVm: AuthViewModel,
    onBack: () -> Unit, // se mantiene para no romper NavGraph
    onGoLogin: (() -> Unit)? = null
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    var registrationMessage by remember { mutableStateOf("") }

    val isFormValid = username.isNotBlank() && email.isNotBlank() && password.isNotBlank()

    // ❌ SIN Scaffold
    // ❌ SIN TopAppBar
    // ✅ La barra morada viene desde el NavGraph
    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Mostrar/Ocultar contraseña"
                    )
                }
            }
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                authVm.register(
                    username = username,
                    email = email,
                    pass = password,
                    role = UserRole.TRABAJADOR
                ) { success ->
                    registrationMessage =
                        if (success) "¡Registro exitoso!"
                        else "El usuario ya existe."
                }
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Usuario")
        }

        if (registrationMessage.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text(registrationMessage)
        }

        if (onGoLogin != null) {
            Spacer(Modifier.height(16.dp))
            TextButton(
                onClick = onGoLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally) // ✅ CENTRADO CORRECTO
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
