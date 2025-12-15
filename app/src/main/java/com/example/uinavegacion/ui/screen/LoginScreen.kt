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
import com.example.uinavegacion.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authVm: AuthViewModel,
    onLoginOk: () -> Unit,
    onGoRegister: () -> Unit
) {
    val focus = LocalFocusManager.current

    var name by rememberSaveable { mutableStateOf("") }
    var pass by rememberSaveable { mutableStateOf("") }
    var showPass by rememberSaveable { mutableStateOf(false) }
    var loginError by remember { mutableStateOf<String?>(null) }

    val isNameValid = name.isNotBlank()
    val isPasswordValid = pass.length > 0 // Simplificamos la validación por ahora
    val isFormValid = isNameValid && isPasswordValid

    val handleLogin = { 
        focus.clearFocus()
        if (isFormValid) {
            authVm.login(name, pass) { success ->
                if (!success) {
                    loginError = "Usuario o contraseña incorrectos"
                }
                // No se necesita onLoginOk(), la UI reaccionará al cambio de estado
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Ingresa tus datos para continuar", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it; loginError = null },
            label = { Text("Nombre de usuario") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = pass,
            onValueChange = { pass = it; loginError = null },
            label = { Text("Contraseña") },
            singleLine = true,
            isError = loginError != null,
            supportingText = {
                if (loginError != null) {
                    Text(loginError!!)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPass = !showPass }) {
                    Icon(imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = if (showPass) "Ocultar" else "Mostrar")
                }
            },
            keyboardActions = KeyboardActions(onDone = { handleLogin() }),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = handleLogin,
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar sesión")
        }

        Spacer(Modifier.height(8.dp))

        TextButton(onClick = onGoRegister) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}