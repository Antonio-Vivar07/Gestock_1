package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.uinavegacion.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginOk: () -> Unit,
    onGoRegister: () -> Unit
) {
    val focus = LocalFocusManager.current
    val authVm: AuthViewModel = viewModel()

    var name by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var showPass by rememberSaveable { mutableStateOf(false) }

    // --- LÓGICA DE VALIDACIÓN CORREGIDA ---
    val isNameValid = name.all { it.isLetter() }
    val isPasswordValid = pass.length == 6 && pass.all { it.isDigit() }
    val isFormValid = isNameValid && name.isNotBlank() && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ingresa tus datos para continuar",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre de usuario") },
            singleLine = true,
            isError = !isNameValid && name.isNotEmpty(),
            supportingText = { 
                if (!isNameValid && name.isNotEmpty()) {
                    Text("El nombre solo debe contener letras")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { 
                // Permite solo 6 dígitos como máximo
                if (it.all { char -> char.isDigit() } && it.length <= 6) {
                    pass = it
                }
            },
            label = { Text("Contraseña") },
            singleLine = true,
            isError = !isPasswordValid && pass.isNotEmpty(),
            supportingText = {
                if (!isPasswordValid && pass.isNotEmpty()) {
                    Text("Debe ser de 6 dígitos numéricos")
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword, imeAction = ImeAction.Done),
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(
                        imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPass) "Ocultar" else "Mostrar"
                    )
                }
            },
            keyboardActions = KeyboardActions(onDone = { 
                focus.clearFocus()
                if (isFormValid) {
                    authVm.login(name)
                    onLoginOk()
                }
            }),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                focus.clearFocus()
                authVm.login(name)
                onLoginOk()
            },
            enabled = isFormValid, 
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }
    }
}