package com.example.proyectofinal.auth

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.proyectofinal.auth.data.model.LoginRequestDto
import com.example.proyectofinal.auth.data.model.LoginResponseDto
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.domain.provider.AuthRemoteProvider
import com.example.proyectofinal.auth.domain.usecases.GetMeUseCase
import com.example.proyectofinal.auth.domain.usecases.PostLoginUseCase
import com.example.proyectofinal.auth.presentation.viewmodel.LoginViewModel
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.core.util.UiState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class LoginViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var tokenManager: TokenManager
    private lateinit var getMeUseCase: GetMeUseCase
    private lateinit var postLoginUseCase: PostLoginUseCase
    private lateinit var authRemoteProvider: AuthRemoteProvider
    private lateinit var viewModel: LoginViewModel

    private val testEmail = "test@example.com"
    private val testPassword = "password123"
    private val loginResponse = LoginResponseDto("dummy_token", "expiration", "user_id_123")
    private val userResponse =
        UserResponseDto("user_id_123", "Test User", "test@example.com", listOf(""))

    @Before
    fun setup() {
        authRemoteProvider = mockk(relaxed = true)
        tokenManager = mockk(relaxed = true)
        getMeUseCase = GetMeUseCase(authRemoteProvider)
        postLoginUseCase = PostLoginUseCase(authRemoteProvider)

        Dispatchers.setMain(testDispatcher)

        viewModel = spyk(LoginViewModel(tokenManager, getMeUseCase, postLoginUseCase))

        coEvery { tokenManager.token } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init calls checkExistingToken and navigates to main if token exists`() = runTest {
        advanceUntilIdle()
        val testToken = "existing_token"
        coEvery { tokenManager.token } returns flowOf(testToken)

        viewModel = LoginViewModel(tokenManager, getMeUseCase, postLoginUseCase)

        val isLoadingStates = mutableListOf<Boolean>()
        val isLoadingJob = launch(testDispatcher) {
            viewModel.isLoading.collect { isLoadingStates.add(it) }
        }

        advanceUntilIdle()

        assertEquals(true, isLoadingStates.first())

        isLoadingJob.cancel()
    }

    @Test
    fun `init calls checkExistingToken and does not navigate to main if token is null`() = runTest {
        advanceUntilIdle()
        coEvery { tokenManager.token } returns flowOf(null)
        every { viewModel["validateEmail"](any<String>()) } returns true

        viewModel = LoginViewModel(tokenManager, getMeUseCase, postLoginUseCase)
        val navigateToMainStates = mutableListOf<Boolean>()
        val job = launch(testDispatcher) {
            viewModel.navigateToMain.collect { navigateToMainStates.add(it) }
        }
        val isLoadingStates = mutableListOf<Boolean>()
        val isLoadingJob = launch(testDispatcher) {
            viewModel.isLoading.collect { isLoadingStates.add(it) }
        }

        advanceUntilIdle()

        assertFalse(navigateToMainStates.last())
        assertEquals(false, navigateToMainStates.first())

        assertEquals(true, isLoadingStates.first())
        assertFalse(isLoadingStates.last())
        assertEquals(false, isLoadingStates[1])

        job.cancel()
        isLoadingJob.cancel()
    }

    @Test
    fun `onLoginClick with empty email or password emits error`() = runTest {
        advanceUntilIdle()
        viewModel.onEmailChange("")
        viewModel.onPasswordChange(testPassword)

        val loginStates = mutableListOf<UiState<LoginResponseDto>>()
        val loginJob =
            launch(testDispatcher) { viewModel.loginState.collect { loginStates.add(it) } }

        advanceUntilIdle()
        assertEquals(UiState.Loading, loginStates.first())

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals(2, loginStates.size)
        assertTrue(loginStates.last() is UiState.Error)
        assertEquals("Email o contraseña vacíos", (loginStates.last() as UiState.Error).message)

        loginJob.cancel()
    }

    @Test
    fun `onLoginClick successful login and user fetch navigates to preferences`() = runTest {
        advanceUntilIdle()
        every { viewModel["validateEmail"](any<String>()) } returns true

        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(testPassword)
        viewModel.onEmailFocusChange(false)
        viewModel.onPasswordFocusChange(false)

        coEvery {
            authRemoteProvider.postLogin(
                LoginRequestDto(
                    testEmail,
                    testPassword
                )
            )
        } returns flowOf(
            NetworkResponse.Success(loginResponse)
        )

        coEvery { authRemoteProvider.getMe() } returns flowOf(
            NetworkResponse.Success(userResponse)
        )

        val isLoadingStates = mutableListOf<Boolean>()
        val isLoadingJob =
            launch(testDispatcher) { viewModel.isLoading.collect { isLoadingStates.add(it) } }

        val loginStates = mutableListOf<UiState<LoginResponseDto>>()
        val loginJob =
            launch(testDispatcher) { viewModel.loginState.collect { loginStates.add(it) } }

        val navigateToPreferencesStates = mutableListOf<Boolean>()
        val navigateToPreferencesJob = launch(testDispatcher) {
            viewModel.navigateToPreferences.collect {
                navigateToPreferencesStates.add(it)
            }
        }

        advanceUntilIdle()
        assertFalse(isLoadingStates.first())
        assertFalse(navigateToPreferencesStates.first())

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals(1, isLoadingStates.size)
        assertFalse(isLoadingStates.last())

        assertEquals(2, loginStates.size)
        assertTrue(loginStates.first() is UiState.Loading)
        assertTrue(loginStates.last() is UiState.Success)
        assertEquals(loginResponse, (loginStates.last() as UiState.Success).data)

        assertEquals(2, navigateToPreferencesStates.size)
        assertTrue(navigateToPreferencesStates.last())

        coVerify(exactly = 1) { tokenManager.saveToken(loginResponse.token) }
        coVerify(exactly = 1) { tokenManager.saveUserId(loginResponse.userId) }
        coVerify(exactly = 1) { tokenManager.saveUser(userResponse) }
        coVerify(exactly = 1) { postLoginUseCase.invoke(LoginRequestDto(testEmail, testPassword)) }
        coVerify(exactly = 1) { getMeUseCase.invoke() }

        isLoadingJob.cancel()
        loginJob.cancel()
        navigateToPreferencesJob.cancel()
    }

    @Test
    fun `onLoginClick fails due to network error and shows error dialog`() = runTest {
        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(testPassword)

        val errorMessage = "Bad Credentials"
        coEvery { authRemoteProvider.postLogin(any()) } returns flowOf(
            NetworkResponse.Failure(errorMessage)
        )

        val isLoadingStates = mutableListOf<Boolean>()
        val isLoadingJob =
            launch(testDispatcher) { viewModel.isLoading.collect { isLoadingStates.add(it) } }

        val loginStates = mutableListOf<UiState<LoginResponseDto>>()
        val loginJob =
            launch(testDispatcher) { viewModel.loginState.collect { loginStates.add(it) } }

        val showErrorDialogStates = mutableListOf<Boolean>()
        val showErrorDialogJob = launch(testDispatcher) {
            viewModel.showErrorDialog.collect {
                showErrorDialogStates.add(it)
            }
        }

        advanceUntilIdle()
        assertEquals(false, isLoadingStates.first())
        assertEquals(UiState.Loading, loginStates.first())
        assertFalse(showErrorDialogStates.first())

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertEquals(1, isLoadingStates.size)
        assertEquals(false, isLoadingStates.first())

        assertEquals(2, loginStates.size)
        assertTrue(loginStates.last() is UiState.Error)
        assertTrue((loginStates.last() as UiState.Error).message.contains("Ocurrió un error desconocido"))

        assertEquals(2, showErrorDialogStates.size)
        assertTrue(showErrorDialogStates.last())

        coVerify(exactly = 1) { postLoginUseCase.invoke(LoginRequestDto(testEmail, testPassword)) }
        coVerify(exactly = 0) { getMeUseCase.invoke() }

        isLoadingJob.cancel()
        loginJob.cancel()
        showErrorDialogJob.cancel()
    }

    @Test
    fun `onLoginClick success but getMeUseCase fails`() = runTest {
        viewModel.onEmailChange(testEmail)
        viewModel.onPasswordChange(testPassword)

        coEvery { authRemoteProvider.postLogin(any()) } returns flowOf(
            NetworkResponse.Success(loginResponse)
        )
        val getMeErrorMessage = "Failed to fetch user data"
        coEvery { authRemoteProvider.getMe() } returns flowOf(
            NetworkResponse.Failure(getMeErrorMessage)
        )

        val isLoadingStates = mutableListOf<Boolean>()
        val isLoadingJob =
            launch(testDispatcher) { viewModel.isLoading.collect { isLoadingStates.add(it) } }

        val navigateToPreferencesStates = mutableListOf<Boolean>()
        val navigateToPreferencesJob = launch(testDispatcher) {
            viewModel.navigateToPreferences.collect {
                navigateToPreferencesStates.add(it)
            }
        }

        advanceUntilIdle()
        assertEquals(false, isLoadingStates.first())
        assertFalse(navigateToPreferencesStates.first())

        viewModel.onLoginClick()
        advanceUntilIdle()

        assertFalse(isLoadingStates.last())

        assertEquals(2, navigateToPreferencesStates.size)
        assertTrue(navigateToPreferencesStates.last())

        coVerify(exactly = 1) { tokenManager.saveToken(loginResponse.token) }
        coVerify(exactly = 1) { tokenManager.saveUserId(loginResponse.userId) }
        coVerify(exactly = 0) { tokenManager.saveUser(any()) }
        coVerify(exactly = 1) { postLoginUseCase.invoke(LoginRequestDto(testEmail, testPassword)) }
        coVerify(exactly = 1) { getMeUseCase.invoke() }

        isLoadingJob.cancel()
        navigateToPreferencesJob.cancel()
    }

    @Test
    fun `onEmailChange updates email state`() = runTest {
        val newEmail = "new@example.com"
        viewModel.onEmailChange(newEmail)
        assertEquals(newEmail, viewModel.email.value)
    }

    @Test
    fun `onEmailChange with touched state validates email`() = runTest {
        val validEmail = "valid@example.com"
        val invalidEmail = "invalid"

        every { viewModel["validateEmail"](any<String>()) } returns true
        every { viewModel["validateEmail"]("valid@example.com") } returns true
        every { viewModel["validateEmail"]("invalid") } returns false

        viewModel.onEmailFocusChange(false)
        advanceUntilIdle()
        assertTrue(viewModel.emailTouched.value)

        viewModel.onEmailChange(validEmail)
        advanceUntilIdle()
        assertNull(viewModel.emailError.value)

        viewModel.onEmailChange(invalidEmail)
        advanceUntilIdle()
        assertNotNull(viewModel.emailError.value)
        assertEquals("Email inválido", viewModel.emailError.value)
    }

    @Test
    fun `onEmailFocusChange validates email when focus lost`() = runTest {
        val invalidEmail = "test"
        val validEmail = "valid@example.com"
        every { viewModel["validateEmail"](any<String>()) } returns true
        every { viewModel["validateEmail"](invalidEmail) } returns false
        every { viewModel["validateEmail"](validEmail) } returns true
        viewModel.onEmailChange(invalidEmail)
        assertFalse(viewModel.emailTouched.value)

        viewModel.onEmailFocusChange(false)
        advanceUntilIdle()

        assertTrue(viewModel.emailTouched.value)
        assertNotNull(viewModel.emailError.value)
        assertEquals("Email inválido", viewModel.emailError.value)

        viewModel.onEmailChange(validEmail)
        viewModel.onEmailFocusChange(false)
        advanceUntilIdle()
        assertNull(viewModel.emailError.value)
    }

    @Test
    fun `onPasswordChange updates password state`() = runTest {
        val newPassword = "newPass"
        viewModel.onPasswordChange(newPassword)
        assertEquals(newPassword, viewModel.password.value)
    }

    @Test
    fun `onPasswordChange with touched state validates password`() = runTest {
        val validPassword = "long_enough_password"
        val invalidPassword = "short"

        viewModel.onPasswordFocusChange(false)
        advanceUntilIdle()
        assertTrue(viewModel.passwordTouched.value)

        viewModel.onPasswordChange(validPassword)
        advanceUntilIdle()
        assertNull(viewModel.passwordError.value)

        viewModel.onPasswordChange(invalidPassword)
        advanceUntilIdle()
        assertNotNull(viewModel.passwordError.value)
        assertEquals("Mínimo 6 caracteres", viewModel.passwordError.value)
    }

    @Test
    fun `onPasswordFocusChange validates password when focus lost`() = runTest {
        val invalidPassword = "abc"
        viewModel.onPasswordChange(invalidPassword)
        assertFalse(viewModel.passwordTouched.value)

        viewModel.onPasswordFocusChange(false)
        advanceUntilIdle()

        assertTrue(viewModel.passwordTouched.value)
        assertNotNull(viewModel.passwordError.value)
        assertEquals("Mínimo 6 caracteres", viewModel.passwordError.value)

        viewModel.onPasswordChange("long_enough_pass")
        viewModel.onPasswordFocusChange(false)
        advanceUntilIdle()
        assertNull(viewModel.passwordError.value)
    }

    @Test
    fun `dismissErrorDialog sets showErrorDialog to false`() = runTest {
        viewModel.onEmailChange("invalid")
        viewModel.onPasswordChange("short")

        coEvery { postLoginUseCase.invoke(any()) } returns flowOf(NetworkResponse.Failure("error"))
        viewModel.onLoginClick()
        advanceUntilIdle()

        assertTrue(viewModel.showErrorDialog.value)

        viewModel.dismissErrorDialog()
        advanceUntilIdle()

        assertFalse(viewModel.showErrorDialog.value)
    }

    @Test
    fun `updateNavigateToMain updates its state`() = runTest {
        viewModel.updateNavigateToMain(false)
        advanceUntilIdle()
        assertFalse(viewModel.navigateToMain.first())
    }
}