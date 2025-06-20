package com.example.proyectofinal.task_student

import android.util.Log
import com.example.proyectofinal.audio.domain.repository.AudioRepository
import com.example.proyectofinal.audio.player.AudioPlayerManager
import com.example.proyectofinal.audio.recorder.AudioRecorderManager
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsMp3UseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsPdfUseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsTxtUseCase
import com.example.proyectofinal.task_student.presentation.tts.TextToSpeechManager
import com.example.proyectofinal.task_student.presentation.viewmodel.TaskStudentViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class TaskStudentViewModelTest {

    private val ttsManager: TextToSpeechManager = mockk(relaxed = true)
    private val audioRecorderManager: AudioRecorderManager = mockk(relaxed = true)
    private val audioPlayerManager: AudioPlayerManager = mockk(relaxed = true)
    private val audioRepository: AudioRepository = mockk(relaxed = true)
    private val orderRepository: OrderRepository = mockk(relaxed = true)
    private val getOrders: GetTaskGroupByStudentUseCase = mockk(relaxed = true)
    private val tokenManager: TokenManager = mockk(relaxed = true)

    private lateinit var viewModel: TaskStudentViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        Dispatchers.setMain(testDispatcher)
        coEvery { ttsManager.isStoped } returns MutableStateFlow(true)
        coEvery { tokenManager.userId } returns flowOf("example_id")
        viewModel = TaskStudentViewModel(
            ttsManager = ttsManager,
            audioRecorderManager = audioRecorderManager,
            audioPlayerManager = audioPlayerManager,
            audioRepositoryImpl = audioRepository,
            getOrders = getOrders,
            tokenManager = tokenManager,
            downloadAsPdfUseCase = DownloadAsPdfUseCase(),
            downloadAsMp3UseCase = DownloadAsMp3UseCase(),
            downloadAsTxtUseCase = DownloadAsTxtUseCase(),
            orderRepository = orderRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when nextPage is called, currentPageIndex is incremented`() = runTest {
        viewModel.nextPage()
        assertEquals(0, viewModel.currentPageIndex.value)
    }

    @Test
    fun `when previousPage is called, currentPageIndex is decremented`() = runTest {
        viewModel.nextPage()
        viewModel.previousPage()
        assertEquals(0, viewModel.currentPageIndex.value)
    }

    @Test
    fun `when speakCurrentPage is called, ttsManager speak is invoked`() = runTest {
        viewModel.speakCurrentPage()
        coVerify { ttsManager.speak(any()) }
    }

    @Test
    fun `when startRecording is called, audioRecorderManager startRecording is invoked`() = runTest {
        viewModel.startRecording()
        verify { audioRecorderManager.startRecording() }
    }

    @Test
    fun `when stopRecording is called, audioRecorderManager stopRecording is invoked`() = testScope.runTest {
        val mockFile = mockk<File>(relaxed = true)
        every { mockFile.exists() } returns true
        every { mockFile.length() } returns 100L
        every { audioRecorderManager.stopRecording() } returns mockFile

        viewModel.stopRecording()

        testDispatcher.scheduler.advanceUntilIdle()

        verify { audioRecorderManager.stopRecording() }
        coVerify { audioRepository.saveAudio(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `when playAudio is called, audioPlayerManager play is invoked`() = runTest {
        val filePath = "test_path"
        every { audioPlayerManager.isPlaying(filePath) } returns false

        viewModel.playAudio(filePath)

        verify { audioPlayerManager.play(filePath, any()) }
        viewModel.stopAudio()
    }

    @Test
    fun `when stopAudio is called, audioPlayerManager stop is invoked`() = runTest {
        viewModel.stopAudio()
        verify { audioPlayerManager.stop() }
    }

    @Test
    fun `when deleteComment is called, audioRepository deleteAudio is invoked`() = testScope.runTest {
        val filePath = "test_path"
        viewModel.deleteComment(filePath)
        testDispatcher.scheduler.advanceUntilIdle()


        coVerify { audioRepository.deleteAudio(filePath) }
    }

    @Test
    fun `when fontSizeIncrease is called, fontSize is incremented`() = runTest {
        viewModel.fontSizeIncrease()
        assertTrue(viewModel.fontSize.value.value > 28)
    }

    @Test
    fun `when fontSizeDecrease is called, fontSize is decremented`() = runTest {
        viewModel.fontSizeDecrease()
        assertTrue(viewModel.fontSize.value.value < 52)
    }
}