package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.remote.RemoteUser
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole

@Composable
fun UserManagementScreen(authVm: AuthViewModel) {
    val session by authVm.session.collectAsState()
    val users by authVm.remoteUsers.collectAsState()
    val loading by authVm.usersLoading.collectAsState()
    val error by authVm.usersError.collectAsState()

    LaunchedEffect(Unit) {
        authVm.loadRemoteUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Gestión de Usuarios",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Administrador: ${session?.username ?: "-"}",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        if (loading) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (error != null) {
            Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { authVm.loadRemoteUsers() }) { Text("Reintentar") }
            return
        }

        Text(
            text = "Usuarios Registrados",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(users) { user ->
                UserCard(
                    user = user,
                    onToggleRole = { targetRole ->
                        authVm.setUserRole(user.username, targetRole)
                    }
                )
            }
        }
    }
}

@Composable
private fun UserCard(
    user: RemoteUser,
    onToggleRole: (UserRole) -> Unit
) {
    val role = remember(user.role) {
        runCatching { UserRole.valueOf(user.role) }.getOrDefault(UserRole.TRABAJADOR)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = user.username, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(2.dp))
                    Text(text = user.email, style = MaterialTheme.typography.bodySmall)
                }

                Text(
                    text = role.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(10.dp))

            val (btnText, targetRole) = if (role == UserRole.TRABAJADOR) {
                "Hacer administrador" to UserRole.ADMINISTRADOR
            } else {
                "Hacer trabajador" to UserRole.TRABAJADOR
            }

            Button(
                onClick = { onToggleRole(targetRole) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(btnText)
            }
        }
    }
}
