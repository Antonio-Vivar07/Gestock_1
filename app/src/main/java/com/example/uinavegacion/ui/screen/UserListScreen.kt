package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.viewmodel.AuthViewModel
import com.example.uinavegacion.viewmodel.UserRole
import kotlinx.coroutines.launch

@Composable
fun UserListScreen(authVm: AuthViewModel) {
    val users by authVm.users.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    // Sincronización automática al entrar en la pantalla
    LaunchedEffect(key1 = Unit) {
        scope.launch {
            try {
                authVm.syncUsers()
            } catch (e: Exception) {
                // Manejar error de sincronización si es necesario
            }
        }
    }

    Scaffold {
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
                    UserListItem(user = user)
                }
            }
        }
    }
}

// --- ¡COMPOSABLE AÑADIDO QUE FALTABA! ---
@Composable
fun UserListItem(user: UserEntity) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(user.username, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodySmall)
            }
            Text(user.role.name, color = if (user.role == UserRole.ADMINISTRADOR) MaterialTheme.colorScheme.primary else Color.Gray)
        }
    }
}
