//package com.example.proyectofinal.navigation
//
//import com.example.proyectofinal.auth.data.model.UserResponseDto
//import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
//import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
//import com.example.proyectofinal.core.network.NetworkResponse
//import com.example.proyectofinal.core.util.UiState
//import com.example.proyectofinal.navigation.presentation.viewmodel.MainScreenUiState
//import com.example.proyectofinal.navigation.presentation.viewmodel.MainViewModel
//import io.mockk.coEvery
//import io.mockk.coVerify
//import io.mockk.every
//import io.mockk.mockk
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.TestScope
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//
//@ExperimentalCoroutinesApi
//class MainViewModelTest {
//
//    private lateinit var authRemoteProvider: AuthRemoteProvider
//    private lateinit var tokenManager: TokenManager
//    private lateinit var viewModel: MainViewModel
//    private val testDispatcher = StandardTestDispatcher()
//    private val testScope = TestScope(testDispatcher)
//
//    private val userResponseStub = UserResponseDto("Test", "test@email.com", "Test Test", emptyList())
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//        authRemoteProvider = mockk()
//        tokenManager = mockk()
//        every { tokenManager.token } returns MutableStateFlow("")
//        coEvery { tokenManager.clearAuthData() } returns Unit
//
//        viewModel = MainViewModel(authRemoteProvider, tokenManager)
//    }
//
//    @Test
//    fun `when token is updated, getUserData is called`() = testScope.runTest {
//        val tokenFlow = MutableStateFlow("new_token")
//        every { tokenManager.token } returns tokenFlow
//        coEvery { authRemoteProvider.getMe() } returns flowOf(
//            NetworkResponse.Success(userResponseStub)
//        )
//
//        viewModel = MainViewModel(authRemoteProvider, tokenManager)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        coVerify { authRemoteProvider.getMe() }
//        assertEquals(UiState.Success(userResponseStub), viewModel.userState.value)
//        assertEquals(MainScreenUiState.Authenticated, viewModel.mainScreenUiState.value)
//    }
//
//    @Test
//    fun `when token is empty, state is updated to Unauthenticated`() = testScope.runTest {
//        viewModel = MainViewModel(authRemoteProvider, tokenManager)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        assertEquals(MainScreenUiState.Unauthenticated, viewModel.mainScreenUiState.value)
//    }
//
//    @Test
//    fun `when getUserData fails, state is updated to Unauthenticated`() = testScope.runTest {
//        val invalidToken = "invalid_token"
//        every { tokenManager.token } returns MutableStateFlow(invalidToken)
//        coEvery { authRemoteProvider.getMe() } returns flowOf(
//            NetworkResponse.Failure("Invalid token")
//        )
//
//        viewModel = MainViewModel(authRemoteProvider, tokenManager)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        assertEquals(UiState.Error("Invalid token"), viewModel.userState.value)
//        assertEquals(MainScreenUiState.Unauthenticated, viewModel.mainScreenUiState.value)
//    }
//
//    @Test
//    fun `when logout is called, token is cleared and state is updated`() = testScope.runTest {
//        viewModel.logout()
//
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        coVerify { tokenManager.clearAuthData() }
//    }
//}