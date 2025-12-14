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
import com.example.uinavegacion.data.remote.RemoteUser
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(authVm: AuthViewModel) {
    val session by authVm.session.collectAsState()
    val remoteUsers by authVm.remoteUsers.collectAsState()
    val loading by authVm.usersLoading.collectAsState()
    val error by authVm.usersError.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var searchQuery by remember { mutableStateOf("") }

    val isAdmin = session?.role == UserRole.ADMINISTRADOR

    // ✅ Carga automática al entrar (tu función real)
    LaunchedEffect(Unit) {
        authVm.loadRemoteUsers()
    }

    // ✅ Filtro por búsqueda
    val filteredUsers = remember(remoteUsers, searchQuery) {
        val q = searchQuery.trim()
        if (q.isBlank()) remoteUsers
        else remoteUsers.filter { u ->
            u.username.contains(q, ignoreCase = true) ||
                    u.email.contains(q, ignoreCase = true)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Usuarios Registrados", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = if (isAdmin) "Administrador: ${session?.username ?: ""}" else "Solo lectura",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Button(
                    onClick = { authVm.loadRemoteUsers() },
                    enabled = !loading
                ) {
                    Text(if (loading) "Cargando..." else "Sincronizar")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Buscador
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar por usuario o email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(12.dp))

            // Estado de carga / error
            if (loading) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
                Spacer(Modifier.height(12.dp))
            }

            if (!error.isNullOrBlank()) {
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(12.dp))
            }

            if (filteredUsers.isEmpty() && !loading) {
                Text(
                    "No hay usuarios para mostrar.",
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredUsers, key = { it.id ?: it.username }) { user ->
                        UserListItem(
                            user = user,
                            currentUsername = session?.username,
                            isAdmin = isAdmin,
                            onToggleRole = { targetRole ->
                                authVm.setUserRole(user.username, targetRole) { ok ->
                                    // showSnackbar es suspend → side effect
                                    // guardamos mensaje y lo mostramos con LaunchedEffect
                                }
                            },
                            authVm = authVm,
                            snackbarHostState = snackbarHostState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UserListItem(
    user: RemoteUser,
    currentUsername: String?,
    isAdmin: Boolean,
    onToggleRole: (UserRole) -> Unit,
    authVm: AuthViewModel,
    snackbarHostState: SnackbarHostState
) {
    // ✅ Tu backend manda role como String (ej: "ADMINISTRADOR" / "TRABAJADOR")
    val userRole = remember(user.role) {
        runCatching { UserRole.valueOf(user.role) }.getOrNull() ?: UserRole.TRABAJADOR
    }

    // Un admin no puede cambiarse el rol a sí mismo
    val isNotSelf = currentUsername != null && currentUsername != user.username
    val canChangeRole = isAdmin && isNotSelf

    val targetRole = if (userRole == UserRole.TRABAJADOR) UserRole.ADMINISTRADOR else UserRole.TRABAJADOR
    val buttonText = if (userRole == UserRole.TRABAJADOR) "Hacer administrador" else "Hacer trabajador"

    val roleColor =
        if (userRole == UserRole.ADMINISTRADOR) MaterialTheme.colorScheme.primary else Color.Gray

    var pendingSnack by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(pendingSnack) {
        pendingSnack?.let {
            snackbarHostState.showSnackbar(it)
            pendingSnack = null
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(user.username, fontWeight = FontWeight.Bold)
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                Text(userRole.name, color = roleColor, fontWeight = FontWeight.SemiBold)
            }

            if (canChangeRole) {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        authVm.setUserRole(user.username, targetRole) { ok ->
                            pendingSnack =
                                if (ok) "Rol de ${user.username} actualizado a ${targetRole.name}"
                                else "Error al actualizar el rol"
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(buttonText)
                }
            }
        }
    }
}
