package com.example.uinavegacion

import com.example.uinavegacion.data.remote.Post
import com.example.uinavegacion.repository.PostRepository
import com.example.uinavegacion.viewmodel.PostViewModel
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    private val repository: PostRepository = mockk()
    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

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
        viewModel.loadPosts()
        testDispatcher.scheduler.advanceUntilIdle() // ← importante

        // Assert
        val currentPosts = viewModel.posts.value
        currentPosts shouldHaveSize 2
        currentPosts[0].title shouldBe "Titulo A"
    }
}
