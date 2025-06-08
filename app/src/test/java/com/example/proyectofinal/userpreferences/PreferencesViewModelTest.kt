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
        coEvery { repository.getUserPreferences() } returns flowOf(UserPreferences(16f, "Default"))
        viewModel = PreferencesViewModel(repository)
    }

    @Test
    fun `when initialized, preferences are loaded`() = runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(UserPreferences(16f, "Default"), viewModel.preferences.value)
    }

    @Test
    fun `updateFontSize updates the font size in repository`() = runTest {
        val newSize = 18f
        coEvery { repository.saveFontSize(newSize) } just Runs

        viewModel.updateFontSize(newSize)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveFontSize(newSize) }
    }

    @Test
    fun `updateFontFamily updates the font family in repository`() = runTest {
        val newFamily = "Serif"
        coEvery { repository.saveFontFamily(newFamily) } just Runs

        viewModel.updateFontFamily(newFamily)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { repository.saveFontFamily(newFamily) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}