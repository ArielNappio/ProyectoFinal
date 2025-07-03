package com.example.proyectofinal.navigation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.usecases.GetMeUseCase
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import com.example.proyectofinal.navigation.presentation.viewmodel.MainScreenUiState
import com.example.proyectofinal.navigation.presentation.viewmodel.MainViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var getMeUseCase: GetMeUseCase
    private lateinit var tokenManager: TokenManager
    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        getMeUseCase = mockk()
        tokenManager = mockk()
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(getMeUseCase, tokenManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refreshSession emits Unauthenticated when token is null`() = runTest {
        coEvery { tokenManager.token } returns flowOf(null)

        val mainScreenUiStates = mutableListOf<MainScreenUiState>()
        val job = launch(testDispatcher) {
            viewModel.mainScreenUiState.collect { mainScreenUiStates.add(it) }
        }

        advanceUntilIdle()
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates.first())

        viewModel.refreshSession()
        advanceUntilIdle()

        assertEquals(2, mainScreenUiStates.size)
        assertEquals(MainScreenUiState.Unauthenticated, mainScreenUiStates[1])

        job.cancel()
    }

    @Test
    fun `refreshSession emits Unauthenticated when token is empty`() = runTest {
        coEvery { tokenManager.token } returns flowOf("")

        val mainScreenUiStates = mutableListOf<MainScreenUiState>()
        val job = launch(testDispatcher) {
            viewModel.mainScreenUiState.collect { mainScreenUiStates.add(it) }
        }
        advanceUntilIdle()
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates.first())

        viewModel.refreshSession()
        advanceUntilIdle()

        assertEquals(2, mainScreenUiStates.size)
        assertEquals(MainScreenUiState.Unauthenticated, mainScreenUiStates[1])

        job.cancel()
    }

    @Test
    fun `refreshSession with valid token succeeds and emits Authenticated`() = runTest {
        val testToken = "valid_token_123"
        val userDto = UserResponseDto("id1", "Test User", "test@example.com", listOf(""))

        coEvery { tokenManager.token } returns flowOf(testToken)
        coEvery { getMeUseCase.invoke() } returns flowOf(
            NetworkResponse.Success(userDto)
        )

        val mainScreenUiStates = mutableListOf<MainScreenUiState>()
        val mainJob = launch(testDispatcher) {
            viewModel.mainScreenUiState.collect { mainScreenUiStates.add(it) }
        }

        val userStates = mutableListOf<UiState<UserResponseDto>>()
        val userJob = launch(testDispatcher) {
            viewModel.userState.collect { userStates.add(it) }
        }

        advanceUntilIdle()
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates.first())
        assertEquals(UiState.Loading, userStates.first())

        viewModel.refreshSession()
        advanceUntilIdle()

        assertEquals(2, mainScreenUiStates.size)
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates[0])
        assertEquals(MainScreenUiState.Authenticated, mainScreenUiStates[1])

        assertEquals(2, userStates.size)
        assertEquals(UiState.Loading, userStates[0])
        assertEquals(UiState.Success(userDto), userStates[1])

        coVerify(exactly = 1) { getMeUseCase.invoke() }

        mainJob.cancel()
        userJob.cancel()
    }

    @Test
    fun `refreshSession with valid token fails and emits Unauthenticated`() = runTest {
        val testToken = "valid_token_123"
        val errorMessage = "Network Error"

        coEvery { tokenManager.token } returns flowOf(testToken)
        coEvery { getMeUseCase.invoke() } returns flowOf(
            NetworkResponse.Failure(errorMessage)
        )

        val mainScreenUiStates = mutableListOf<MainScreenUiState>()
        val mainJob = launch(testDispatcher) {
            viewModel.mainScreenUiState.collect { mainScreenUiStates.add(it) }
        }

        val userStates = mutableListOf<UiState<UserResponseDto>>()
        val userJob = launch(testDispatcher) {
            viewModel.userState.collect { userStates.add(it) }
        }

        advanceUntilIdle()
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates.first())
        assertEquals(UiState.Loading, userStates.first())

        viewModel.refreshSession()
        advanceUntilIdle()

        assertEquals(2, mainScreenUiStates.size)
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates[0])
        assertEquals(MainScreenUiState.Unauthenticated, mainScreenUiStates[1])

        assertEquals(2, userStates.size)
        assertEquals(UiState.Loading, userStates[0])
        assertEquals(UiState.Error(errorMessage), userStates[1])

        coVerify(exactly = 1) { getMeUseCase.invoke() }

        mainJob.cancel()
        userJob.cancel()
    }

    @Test
    fun `refreshSession with valid token and use case throws exception emits Unauthenticated`() = runTest {
        val testToken = "valid_token_123"
        val exceptionMessage = "Simulated crash"

        coEvery { tokenManager.token } returns flowOf(testToken)
        coEvery { getMeUseCase.invoke() } returns flow { throw Exception(exceptionMessage) }

        val mainScreenUiStates = mutableListOf<MainScreenUiState>()
        val mainJob = launch(testDispatcher) {
            viewModel.mainScreenUiState.collect { mainScreenUiStates.add(it) }
        }

        val userStates = mutableListOf<UiState<UserResponseDto>>()
        val userJob = launch(testDispatcher) {
            viewModel.userState.collect { userStates.add(it) }
        }

        advanceUntilIdle()
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates.first())
        assertEquals(UiState.Loading, userStates.first())

        viewModel.refreshSession()
        advanceUntilIdle()

        assertEquals(2, mainScreenUiStates.size)
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates[0])
        assertEquals(MainScreenUiState.Unauthenticated, mainScreenUiStates[1])

        assertEquals(2, userStates.size)
        assertEquals(UiState.Loading, userStates[0])
        assertEquals(UiState.Error(exceptionMessage), userStates[1])

        coVerify(exactly = 1) { getMeUseCase.invoke() }

        mainJob.cancel()
        userJob.cancel()
    }

    @Test
    fun `logout clears auth data and emits Unauthenticated`() = runTest {
        coEvery { tokenManager.clearAuthData() } returns Unit

        val mainScreenUiStates = mutableListOf<MainScreenUiState>()
        val job = launch(testDispatcher) {
            viewModel.mainScreenUiState.collect { mainScreenUiStates.add(it) }
        }
        advanceUntilIdle()
        assertEquals(MainScreenUiState.Loading, mainScreenUiStates.first())

        viewModel.logout()
        advanceUntilIdle()

        assertEquals(2, mainScreenUiStates.size)
        assertEquals(MainScreenUiState.Unauthenticated, mainScreenUiStates[1])

        coVerify(exactly = 1) { tokenManager.clearAuthData() }

        job.cancel()
    }
}