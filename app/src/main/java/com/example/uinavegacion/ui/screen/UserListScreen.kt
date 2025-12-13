package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole
import com.example.uinavegacion.viewmodel.UserSession
import kotlinx.coroutines.launch

@Composable
fun UserListScreen(authVm: AuthViewModel) {
    val users by authVm.users.collectAsState(initial = emptyList())
    val session by authVm.session.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Sincronización automática al entrar en la pantalla
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            try {
                authVm.syncUsers()
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("Error al sincronizar usuarios: ${e.message}")
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        LazyColumn(contentPadding = it, modifier = Modifier.padding(16.dp)) {
            item {
                Text("Usuarios Registrados", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(16.dp))
            }
            if (users.isEmpty()) {
                item {
                    Text("No hay usuarios registrados o no se han podido sincronizar.")
                }
            } else {
                items(users) { user ->
                    UserListItem(
                        user = user,
                        currentUserSession = session,
                        onUpdateRole = {
                            authVm.updateUserRole(user, UserRole.ADMINISTRADOR) { success ->
                                scope.launch {
                                    if(success) {
                                        snackbarHostState.showSnackbar("Rol de ${user.username} actualizado a Administrador")
                                    } else {
                                        snackbarHostState.showSnackbar("Error al actualizar el rol")
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserListItem(
    user: UserEntity,
    currentUserSession: UserSession?,
    onUpdateRole: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(user.username, fontWeight = FontWeight.Bold)
                    Text(user.email, style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    user.role.name,
                    color = if (user.role == UserRole.ADMINISTRADOR) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }

            // Lógica de visibilidad del botón
            val isAdmin = currentUserSession?.role == UserRole.ADMINISTRADOR
            val isTargetTrabajador = user.role == UserRole.TRABAJADOR
            // Un admin no puede cambiarse el rol a sí mismo
            val isNotSelf = currentUserSession?.username != user.username

            if (isAdmin && isTargetTrabajador && isNotSelf) {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = onUpdateRole,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Hacer administrador")
                }
            }
        }
    }
}
