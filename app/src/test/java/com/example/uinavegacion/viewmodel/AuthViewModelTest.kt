package com.example.uinavegacion.viewmodel

import com.example.uinavegacion.data.local.user.UserEntity
import com.example.uinavegacion.repository.UserRepository
import com.example.uinavegacion.testutil.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `login exitoso actualiza session y devuelve true`() = runTest {
        val repo = mockk<UserRepository>()
        val vm = AuthViewModel(repo)

        val expectedUser = UserEntity(
            id = 1,
            username = "demo",
            email = "demo@test.com",
            pass = "1234",
            role = UserRole.ADMINISTRADOR
        )

        coEvery { repo.loginUser("demo", "1234") } returns expectedUser

        var callbackResult: Boolean? = null
        vm.login("demo", "1234") { callbackResult = it }

        advanceUntilIdle()

        assertTrue(callbackResult == true)
        assertEquals(UserSession("demo", UserRole.ADMINISTRADOR), vm.session.value)
    }

    @Test
    fun `logout limpia session y llama clearSession del repositorio`() = runTest {
        val repo = mockk<UserRepository>()
        val vm = AuthViewModel(repo)

        val expectedUser = UserEntity(
            id = 1,
            username = "demo",
            email = "demo@test.com",
            pass = "1234",
            role = UserRole.TRABAJADOR
        )

        coEvery { repo.loginUser(any(), any()) } returns expectedUser
        coEvery { repo.clearSession() } returns Unit

        vm.login("demo", "1234") { }
        advanceUntilIdle()

        vm.logout()
        advanceUntilIdle()

        coVerify(exactly = 1) { repo.clearSession() }
        assertNull(vm.session.value)
    }
}
