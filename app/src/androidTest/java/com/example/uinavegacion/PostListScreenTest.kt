package com.example.uinavegacion

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.uinavegacion.data.remote.FakePostApiService
import com.example.uinavegacion.repository.PostRepository
import com.example.uinavegacion.ui.screen.PostListScreen
import com.example.uinavegacion.viewmodel.PostViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Prueba de UI para PostListScreen usando un PostRepository falso.
 */
@RunWith(AndroidJUnit4::class)
class PostListScreenTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun postsList_se_muestra_al_cargar_desde_api() {
        // 1. Crear el servicio de API falso
        val fakeApi = FakePostApiService()

        // 2. Crear el repositorio con el servicio falso
        val repository = PostRepository(fakeApi)

        // 3. Crear el ViewModel con el repositorio falso
        val viewModel = PostViewModel(repository)

        // 4. Iniciar la pantalla con el ViewModel falso
        composeRule.setContent {
            PostListScreen(viewModel = viewModel)
        }

        // 5. Verificar que el texto del post falso se muestra
        composeRule.onNodeWithText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")
            .assertIsDisplayed()
    }
}
