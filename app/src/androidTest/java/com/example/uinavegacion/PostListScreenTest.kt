package com.example.uinavegacion

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.uinavegacion.ui.screen.PostListScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de UI básica para PostListScreen.
 * Si la pantalla lanza alguna excepción al renderizarse,
 * el test fallará. Si todo va bien, el test pasa.
 */
@RunWith(AndroidJUnit4::class)
class PostListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun postListScreen_se_muestra_sin_crashear() {
        composeRule.setContent {
            PostListScreen()
        }
        // No necesitamos más: si la composición falla, el test marca error.
    }
}
