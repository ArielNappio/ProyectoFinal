package com.example.proyectofinal.userpreferences

import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository
import com.example.proyectofinal.userpreferences.presentation.viewmodel.PreferencesViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesViewModelTest {

    private lateinit var repository: UserPreferencesRepository
    private lateinit var viewModel: PreferencesViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        coEvery { repository.getUserPreferences() } returns flowOf(UserPreferences(26f, "Default", iconSize = 32f))
        viewModel = PreferencesViewModel(repository)
    }

    @Test
    fun `when initialized, preferences are loaded`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(UserPreferences(26f, "Default", iconSize = 32f), viewModel.preferences.value)
    }

    @Test
    fun `updateFontSize updates the font size in repository`() = runTest {
        val newSize = 26f
        coEvery { repository.saveFontSize(newSize) } just Runs
        coEvery { repository.saveIconSize(32f) } just Runs

        viewModel.updateFontSize(newSize)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveFontSize(newSize) }
        coVerify { repository.saveIconSize(32f) }
    }

    @Test
    fun `updateFontFamily updates the font family in repository`() = runTest {
        val newFamily = "Serif"
        coEvery { repository.saveFontFamily(newFamily) } just Runs

        viewModel.updateFontFamily(newFamily)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveFontFamily(newFamily) }
    }

    @Test
    fun `updateProfileImageUri saves the profile image URI in repository`() = runTest {
        val email = "test@example.com"
        val uri = "image_uri"
        coEvery { repository.saveProfileImageUri(email, uri) } just Runs

        viewModel.updateProfileImageUri(email, uri)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveProfileImageUri(email, uri) }
    }

    @Test
    fun `getProfileImageUri retrieves the profile image URI from repository`() = runTest {
        val email = "test@example.com"
        val uri = "image_uri"
        coEvery { repository.getProfileImageUri(email) } returns uri

        var result: String? = null
        viewModel.getProfileImageUri(email) { result = it }
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(uri, result)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}