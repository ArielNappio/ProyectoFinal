package com.example.proyectofinal.camera

import android.graphics.Bitmap
import com.example.proyectofinal.camera.domain.usecases.SavePhotoToGalleryUseCase
import com.example.proyectofinal.camera.presentation.viewmodel.CameraViewModel
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CameraViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
    private lateinit var cameraViewModel: CameraViewModel

    @Before
    fun setUp() {
        savePhotoToGalleryUseCase = mockk(relaxed = true)
        cameraViewModel = CameraViewModel(savePhotoToGalleryUseCase)
    }

    @Test
    fun `storePhotoInGallery should call SavePhotoToGalleryUseCase and update state`() = testScope.runTest {
        val mockBitmap = mockk<Bitmap>()

        cameraViewModel.storePhotoInGallery(mockBitmap)

        coVerify { savePhotoToGalleryUseCase.call(mockBitmap) }

        val state = cameraViewModel.state.first()
        assertEquals(mockBitmap, state.capturedImage)
    }

    // TODO: Ver por qué falla este test
//    @Test
//    fun `onCleared should recycle captured image`() {
//        val mockBitmap = mockk<Bitmap>()
//        cameraViewModel.storePhotoInGallery(mockBitmap)
//
//        cameraViewModel.onCleared()
//
//        coVerify { cameraViewModel.state.value.capturedImage?.recycle() }
//    }
}