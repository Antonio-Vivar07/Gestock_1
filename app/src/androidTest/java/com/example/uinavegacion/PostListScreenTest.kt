package com.example.uinavegacion

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.uinavegacion.ui.screen.PostListScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de UI sencilla para PostListScreen usando Compose UI Test.
 * Nota: Esta prueba realiza una llamada real a la API, por lo que requiere conexión a Internet.
 */
@RunWith(AndroidJUnit4::class)
class PostListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun postsList_se_muestra_al_cargar_desde_api() {
        composeRule.setContent {
            PostListScreen()
        }

        // Verificamos que al menos uno de los títulos típicos de JSONPlaceholder se muestre en pantalla.
        composeRule.onNodeWithText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")
            .assertIsDisplayed()
    }
}
