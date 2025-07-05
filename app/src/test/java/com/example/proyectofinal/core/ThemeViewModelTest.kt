package com.example.proyectofinal.core

import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.presentation.viewmodel.ThemeViewModel
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ThemeViewModelTest {

    private lateinit var viewModel: ThemeViewModel
    private lateinit var mockTokenManager: TokenManager
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        mockTokenManager = mockk(relaxed = true)
        viewModel = ThemeViewModel(mockTokenManager)
    }

    @Test
    fun `test toggleTheme updates isDarkTheme`() = testScope.runTest {
        val initialTheme = viewModel.isDarkTheme.first()
        viewModel.toggleTheme()
        assertEquals(!initialTheme, viewModel.isDarkTheme.first())
    }
}
