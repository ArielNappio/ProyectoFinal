package com.example.proyectofinal.text_editor.presentation.viewmodel

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.core.network.ApiUrls
import com.example.proyectofinal.text_editor.data.repository.PdfRemoteRepositoryImpl
import com.example.proyectofinal.text_editor.domain.PdfBitmapConverter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TextEditorViewModel(
    private val pdfBitmapConverter: PdfBitmapConverter,
    private val pdfRemoteRepository: PdfRemoteRepositoryImpl,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): ViewModel() {

    // Text Editor State
    private val _textState = MutableStateFlow("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum")
    val textState: StateFlow<String> = _textState

    fun updateString(newValue: String) {
        viewModelScope.launch {
            _textState.emit(newValue)
        }
    }

    // PDF Viewer State
    private val _renderedPages = MutableStateFlow<List<Bitmap>>(emptyList())
    val renderedPages: StateFlow<List<Bitmap>> = _renderedPages.asStateFlow()

    fun downloadPdf(context: Context) {
        viewModelScope.launch(dispatcher) {
            pdfRemoteRepository
                .downloadPdf(context, ApiUrls.EXAMPLE_PDF_URL)
                ?.let {
                    _renderedPages.value = pdfBitmapConverter.pdfToBitmaps(it)
                }
        }
    }

    private val _pdfViewScale = MutableStateFlow(1f)
    val pdfViewScale = _pdfViewScale.asStateFlow()

    private val _pdfViewOffsetX = MutableStateFlow(0f)
    val pdfViewOffsetX = _pdfViewOffsetX.asStateFlow()

    private val _pdfViewOffsetY = MutableStateFlow(0f)
    val pdfViewOffsetY = _pdfViewOffsetY.asStateFlow()

    fun updatePdfZoom(zoom: Float, panX: Float, panY: Float) {
        _pdfViewScale.value = (_pdfViewScale.value * zoom).coerceIn(1f, 5f)
        if (_pdfViewScale.value > 1f) {
            _pdfViewOffsetX.value += panX
            _pdfViewOffsetY.value += panY
        } else {
            _pdfViewOffsetX.value = 0f
            _pdfViewOffsetY.value = 0f
        }
    }

    fun resetPdfZoom() {
        _pdfViewScale.value = 1f
        _pdfViewOffsetX.value = 0f
        _pdfViewOffsetY.value = 0f
    }
}