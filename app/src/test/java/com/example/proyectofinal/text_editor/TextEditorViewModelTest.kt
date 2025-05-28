package com.example.proyectofinal.text_editor

import android.graphics.Bitmap
import android.net.Uri
import com.example.proyectofinal.text_editor.domain.PdfBitmapConverter
import com.example.proyectofinal.text_editor.presentation.viewmodel.TextEditorViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TextEditorViewModelTest {

    private lateinit var viewModel: TextEditorViewModel
    private lateinit var mockPdfBitmapConverter: PdfBitmapConverter
    private lateinit var mockUri: Uri
    private lateinit var mockBitmapList: List<Bitmap>
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        mockUri = mockk<Uri>()
        mockBitmapList = listOf(mockk<Bitmap>())
        mockPdfBitmapConverter = mockk()
        coEvery { mockPdfBitmapConverter.pdfToBitmaps(mockUri) } returns mockBitmapList
        viewModel = TextEditorViewModel(mockPdfBitmapConverter, testDispatcher)
    }

    @Test
    fun `test updateString updates textState`() = testScope.runTest {
        val newText = "New text content"
        viewModel.updateString(newText)
        assertEquals(newText, viewModel.textState.first())
    }

    @Test
    fun `test setPdfUri updates pdfUri and renderedPages`() = testScope.runTest {
        viewModel.setPdfUri(mockUri)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockUri, viewModel.pdfUri.first())
        assertEquals(mockBitmapList, viewModel.renderedPages.first())
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