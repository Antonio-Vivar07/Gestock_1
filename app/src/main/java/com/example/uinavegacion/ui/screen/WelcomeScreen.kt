package com.example.uinavegacion.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.uinavegacion.R

@Composable
fun WelcomeScreen(
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Elemento 1: Texto de bienvenida
        Text(
            text = "Bienvenido a la app Gestock",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(24.dp))

        // Elemento 2: Botones en una fila
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = onGoLogin) { Text("Ir a Login") }
            OutlinedButton(onClick = onGoRegister) { Text("Ir a Registro") }
        }

        Spacer(Modifier.height(48.dp))

        // Elemento 3: El logo
        Image(
            painter = painterResource(id = R.drawable.logo_gestock),
            contentDescription = "Logo de Gestock",
            modifier = Modifier.height(230.dp) // <-- TAMAÑO AUMENTADO A 230.DP
        )
    }
}
