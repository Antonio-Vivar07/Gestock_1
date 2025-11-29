package com.example.uinavegacion

import com.example.uinavegacion.data.remote.Post
import com.example.uinavegacion.repository.PostRepository
import com.example.uinavegacion.viewmodel.PostViewModel
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

/**
 * Prueba unitaria de PostViewModel usando Kotest + MockK + coroutines-test.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val repository: PostRepository = mockk()

    @Test
    fun `loadPosts actualiza el estado posts correctamente`() = runTest {
        // Arrange
        val fakePosts = listOf(
            Post(userId = 1, id = 1, title = "Titulo A", body = "Contenido A"),
            Post(userId = 1, id = 2, title = "Titulo B", body = "Contenido B")
        )
        coEvery { repository.getPosts() } returns fakePosts

        val viewModel = PostViewModel(repository)

        // Act
        // La llamada a loadPosts() ya se ejecutó en init, pero la forzamos de nuevo por claridad
        viewModel.loadPosts()

        // Assert
        val currentPosts = viewModel.posts.value
        currentPosts shouldHaveSize 2
        currentPosts[0].title shouldBe "Titulo A"
    }
}
