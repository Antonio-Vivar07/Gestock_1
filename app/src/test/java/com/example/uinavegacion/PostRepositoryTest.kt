package com.example.uinavegacion

import com.example.uinavegacion.data.remote.Post
import com.example.uinavegacion.data.remote.PostApiService
import com.example.uinavegacion.repository.PostRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Prueba unitaria simple de PostRepository usando JUnit4 + MockK.
 * - No toca red real.
 * - No afecta el funcionamiento de la app (solo src/test).
 */
class PostRepositoryTest {

    private val apiService: PostApiService = mockk()

    @Test
    fun getPosts_retorna_la_lista_entregada_por_la_api() = runTest {
        // Arrange
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Titulo 1", body = "Contenido 1"),
            Post(userId = 2, id = 2, title = "Titulo 2", body = "Contenido 2")
        )
        coEvery { apiService.getPosts() } returns fakePosts
        val repository = PostRepository(apiService)

        // Act
        val result = repository.getPosts()

        // Assert
        assertEquals(fakePosts, result)
        coVerify(exactly = 1) { apiService.getPosts() }
    }
}
