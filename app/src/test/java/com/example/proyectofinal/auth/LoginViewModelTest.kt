package com.example.proyectofinal.auth

import com.example.proyectofinal.auth.data.model.LoginRequestDto
import com.example.proyectofinal.auth.data.model.LoginResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.auth.presentation.viewmodel.LoginViewModel
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var viewModel: LoginViewModel
    private lateinit var repository: AuthRemoteProvider
    private lateinit var tokenManager: TokenManager

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        tokenManager = mockk()
        coEvery { tokenManager.token } returns flowOf(null)
        viewModel = LoginViewModel(repository, tokenManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onLoginClick with valid credentials should update loginState to Success`() = testScope.runTest {
        // Arrange
        val email = "test@example.com"
        val password = "password123"
        val responseDto = LoginResponseDto(
            token = "testToken",
            expiration = "testExpiration",
            userId = "testUserId"
        )
        coEvery { repository.postLogin(LoginRequestDto(email, password)) } returns flow { emit(NetworkResponse.Success(responseDto)) }
        coEvery { tokenManager.saveToken(responseDto.token) } just Runs

        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)

        // Act
        viewModel.onLoginClick()

        testDispatcher.scheduler.advanceUntilIdle()
        // Assert
        assertTrue(viewModel.loginState.value is UiState.Success)
        assertEquals(responseDto, (viewModel.loginState.value as UiState.Success).data)
        coVerify { tokenManager.saveToken(responseDto.token) }
    }

    @Test
    fun `onLoginClick with empty credentials should update loginState to Error`() = testScope.runTest {
        // Arrange
        viewModel.onEmailChange("")
        viewModel.onPasswordChange("")

        // Act
        viewModel.onLoginClick()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.loginState.value is UiState.Error)
        assertEquals("Email o contraseña vacíos", (viewModel.loginState.value as UiState.Error).message)
    }

    @Test
    fun `checkExistingToken with valid token should navigate to main screen`() = testScope.runTest {
        // Arrange
        val token = "validToken"
        coEvery { tokenManager.token } returns flowOf(token)
        viewModel = LoginViewModel(repository, tokenManager)

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(viewModel.navigateToMain.value)
    }

    @Test
    fun `checkExistingToken with no token should not navigate to main screen`() = testScope.runTest {
        // Arrange
        every { tokenManager.token } returns flow { emit(null) }

        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assertTrue(!viewModel.navigateToMain.value)
    }

}