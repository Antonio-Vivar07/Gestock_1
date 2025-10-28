package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authVm: AuthViewModel,
    onGoLogin: (() -> Unit)? = null
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.TRABAJADOR) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    var registrationMessage by remember { mutableStateOf<String?>(null) }

    val isUsernameValid = username.all { it.isLetter() }
    val isEmailValid = email.contains("@") && email.contains(".")
    val isPasswordValid = password.length == 6 && password.all { it.isDigit() }
    val isFormValid = isUsernameValid && username.isNotBlank() && isEmailValid && email.isNotBlank() && isPasswordValid

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Registro de Nuevo Usuario", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it; registrationMessage = null },
            label = { Text("Nombre de Usuario") },
            singleLine = true,
            isError = !isUsernameValid && username.isNotEmpty(),
            supportingText = { if (!isUsernameValid && username.isNotEmpty()) { Text("El nombre solo debe contener letras.") } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; registrationMessage = null },
            label = { Text("Email") },
            singleLine = true,
            isError = !isEmailValid && email.isNotEmpty(),
            supportingText = { if (!isEmailValid && email.isNotEmpty()) { Text("Debe ser un email válido.") } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { 
                if (it.all { c -> c.isDigit() } && it.length <= 6) {
                    password = it
                    registrationMessage = null
                }
            },
            label = { Text("Contraseña") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showPassword) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = isRoleDropdownExpanded,
            onExpandedChange = { isRoleDropdownExpanded = !isRoleDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedRole.name,
                onValueChange = {},
                readOnly = true,
                label = { Text("Rol de Usuario") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = isRoleDropdownExpanded,
                onDismissRequest = { isRoleDropdownExpanded = false }
            ) {
                UserRole.values().forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.name) },
                        onClick = { 
                            selectedRole = role 
                            isRoleDropdownExpanded = false
                        }
                    )
                }
            }
        }

        if (registrationMessage != null) {
            Spacer(Modifier.height(16.dp))
            Text(registrationMessage!!, color = if (registrationMessage!!.contains("exitoso")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                // --- CORRECCIÓN DEFINITIVA: Se pasa el callback a la función register ---
                authVm.register(username, email, password, selectedRole) { success ->
                    registrationMessage = if (success) "¡Registro exitoso!" else "El usuario ya existe."
                }
            },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Registrar Usuario")
        }

        if (onGoLogin != null) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onGoLogin) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}