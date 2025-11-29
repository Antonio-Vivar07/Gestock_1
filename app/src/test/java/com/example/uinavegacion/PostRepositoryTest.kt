package com.example.uinavegacion

import com.example.uinavegacion.data.remote.Post
import com.example.uinavegacion.data.remote.PostApiService
import com.example.uinavegacion.repository.PostRepository
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Prueba unitaria de PostRepository usando Kotest + MockK.
 */
class PostRepositoryTest {

    private val apiService: PostApiService = mockk()

    @Test
    fun `getPosts debe retornar la lista entregada por la API`() = runTest {
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
        result shouldHaveSize 2
        result.shouldContainExactly(fakePosts)
        result[0].title shouldBe "Titulo 1"
    }
}
