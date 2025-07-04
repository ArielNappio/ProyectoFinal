package com.example.proyectofinal.student

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.proyectofinal.audio.data.repository.AudioRepositoryImpl
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.player.AudioPlayerManager
import com.example.proyectofinal.student.presentation.viewmodel.CommentsViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class CommentsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var audioRepositoryImpl: AudioRepositoryImpl
    private lateinit var audioPlayerManager: AudioPlayerManager
    private lateinit var viewModel: CommentsViewModel

    private val audio1 = RecordedAudio(1, filePath = "/path/to/audio1.mp3", title = "Comment 1", timestamp = 10000L, duration =  1L, associatedTaskId = "1", date = "2025-10-01")
    private val audio2 = RecordedAudio(2, filePath = "/path/to/audio2.mp3", title = "Comment 2", timestamp = 12000L, duration =  2L, associatedTaskId = "1", date = "2025-10-01")

    @Before
    fun setup() {
        audioRepositoryImpl = mockk(relaxed = true)
        audioPlayerManager = mockk(relaxed = true)

        Dispatchers.setMain(testDispatcher)

        viewModel = CommentsViewModel(audioRepositoryImpl, audioPlayerManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `deleteComment removes audio from repository and list`() = runTest {
        val initialComments = listOf(audio1, audio2)
        val taskId = "1"

        coEvery { audioRepositoryImpl.getAudiosForTask(taskId) } returns initialComments
        viewModel.loadCommentsByTaskId(1)
        advanceUntilIdle()
        assertEquals(initialComments, viewModel.comments.value)

        coEvery { audioRepositoryImpl.deleteAudio(audio1.filePath) } just Runs

        viewModel.deleteComment(audio1.filePath)
        advanceUntilIdle()

        assertEquals(listOf(audio2), viewModel.comments.value)
        coVerify(exactly = 1) { audioRepositoryImpl.deleteAudio(audio1.filePath) }
    }

    @Test
    fun `loadCommentsByTaskId updates comments list`() = runTest {
        val taskId = 1
        val commentsFromRepo = listOf(audio1, audio2)

        coEvery { audioRepositoryImpl.getAudiosForTask("1") } returns commentsFromRepo

        viewModel.loadCommentsByTaskId(taskId)
        advanceUntilIdle()

        assertEquals(commentsFromRepo, viewModel.comments.value)
        coVerify(exactly = 1) { audioRepositoryImpl.getAudiosForTask("1") }
    }

    @Test
    fun `confirmEditName updates audio name and closes dialog`() = runTest {
        val initialComments = listOf(audio1)
        val newName = "New Name For Comment 1"
        val taskId = "task1"

        coEvery { audioRepositoryImpl.getAudiosForTask(taskId) } returns initialComments
        coEvery { audioRepositoryImpl.updateAudioName(audio1.filePath, newName) } just Runs

        viewModel.loadCommentsByTaskId(1)
        advanceUntilIdle()
        viewModel.openEditDialog(audio1.filePath, audio1.title)
        advanceUntilIdle()
        viewModel.updateEditingName(newName)
        advanceUntilIdle()

        viewModel.confirmEditName()
        advanceUntilIdle()

        assertTrue(viewModel.showEditDialog.value)
    }

    @Test
    fun `confirmEditName does not update if new name is blank`() = runTest {
        val initialComments = listOf(audio1)
        val taskId = "task1"

        coEvery { audioRepositoryImpl.getAudiosForTask(taskId) } returns initialComments
        viewModel.loadCommentsByTaskId(1)
        advanceUntilIdle()
        viewModel.openEditDialog(audio1.filePath, audio1.title)
        advanceUntilIdle()
        viewModel.updateEditingName(" ")
        advanceUntilIdle()

        viewModel.confirmEditName()
        advanceUntilIdle()

        coVerify(exactly = 0) { audioRepositoryImpl.updateAudioName(any(), any()) }
        assertTrue(viewModel.showEditDialog.value)
    }

    @Test
    fun `seekTo updates current position`() = runTest {
        val targetPosition = 5000L
        val path = audio1.filePath

        every { audioPlayerManager.seekTo(targetPosition) } just Runs

        viewModel.seekTo(targetPosition, false, path)
        advanceUntilIdle()

        assertEquals(targetPosition, viewModel.currentPosition.value)
        coVerify(exactly = 1) { audioPlayerManager.seekTo(targetPosition) }
        coVerify(exactly = 0) { audioPlayerManager.play(any(), any()) }
    }
}