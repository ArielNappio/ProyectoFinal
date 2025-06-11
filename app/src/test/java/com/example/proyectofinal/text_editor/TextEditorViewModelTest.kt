package com.example.proyectofinal.text_editor

import android.content.Context
import android.graphics.Bitmap
import com.example.proyectofinal.student.domain.usecase.GetTaskById
import com.example.proyectofinal.text_editor.data.repository.PdfProviderImpl
import com.example.proyectofinal.text_editor.domain.PdfBitmapConverter
import com.example.proyectofinal.text_editor.presentation.viewmodel.TextEditorViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class TextEditorViewModelTest {

    private lateinit var viewModel: TextEditorViewModel
    private lateinit var mockPdfBitmapConverter: PdfBitmapConverter
    private lateinit var mockContext: Context
    private lateinit var mockFile: File
    private lateinit var mockBitmapList: List<Bitmap>
    private lateinit var mockPdfRemoteRepository: PdfProviderImpl
    private lateinit var mockGetTaskById: GetTaskById
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockContext = mockk()
        mockFile = mockk()
        mockBitmapList = listOf(mockk())
        mockPdfBitmapConverter = mockk()
        mockPdfRemoteRepository = mockk()
        mockGetTaskById = mockk()
        coEvery { mockPdfBitmapConverter.pdfToBitmaps(mockFile) } returns mockBitmapList
        coEvery { mockPdfRemoteRepository.downloadPdf(mockContext, any()) } returns mockFile
        viewModel = TextEditorViewModel(mockPdfBitmapConverter, mockPdfRemoteRepository, mockGetTaskById, testDispatcher)
    }

    @Test
    fun `test updateViewsWeights updates top and bottom view weights`() = testScope.runTest {
        viewModel.updateViewsWeights(0.7f, 0.3f)
        assertEquals(0.7f, viewModel.topViewWeight.first())
        assertEquals(0.3f, viewModel.bottomViewWeight.first())
    }

    @Test
    fun `test downloadPdf updates renderedPages`() = testScope.runTest {
        viewModel.downloadPdf(mockContext)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(mockBitmapList, viewModel.renderedPages.first())
    }

    @Test
    fun `test updateString updates textState`() = testScope.runTest {
        val newText = "Updated text"
        viewModel.updateString(newText)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(newText, viewModel.textState.first())
    }

    @Test
    fun `test updatePdfZoom updates scale and offsets`() = testScope.runTest {
        viewModel.updatePdfZoom(2f, 10f, 20f)
        assertEquals(2f, viewModel.pdfViewScale.first())
        assertEquals(10f, viewModel.pdfViewOffsetX.first())
        assertEquals(20f, viewModel.pdfViewOffsetY.first())
    }

    @Test
    fun `test resetPdfZoom resets scale and offsets`() = testScope.runTest {
        viewModel.updatePdfZoom(2f, 10f, 20f)
        viewModel.resetPdfZoom()
        assertEquals(1f, viewModel.pdfViewScale.first())
        assertEquals(0f, viewModel.pdfViewOffsetX.first())
        assertEquals(0f, viewModel.pdfViewOffsetY.first())
    }
}