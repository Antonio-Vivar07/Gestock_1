package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreatePostScreen(
    onPostCreated: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Crear nuevo Post")

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Nombre (title)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = body,
            onValueChange = { body = it },
            label = { Text("Código QR (body)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onPostCreated(title, body) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar")
        }
    }
}