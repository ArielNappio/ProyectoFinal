package com.example.proyectofinal.student

import com.example.proyectofinal.audio.data.repository.AudioRepositoryImpl
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.player.AudioPlayerManager
import com.example.proyectofinal.student.presentation.viewmodel.CommentsViewModel
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CommentsViewModelTest {

    private lateinit var audioRepositoryImpl: AudioRepositoryImpl
    private lateinit var audioPlayerManager: AudioPlayerManager
    private lateinit var viewModel: CommentsViewModel

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        audioRepositoryImpl = mockk(relaxed = true)
        audioPlayerManager = mockk(relaxed = true)
        viewModel = CommentsViewModel(audioRepositoryImpl, audioPlayerManager)
    }

    @Test
    fun `loadCommentsByTaskId should update comments state`() = testScope.runTest {
        val taskId = 1
        val mockComments = listOf(
            RecordedAudio(
                id = 1,
                filePath = "path1",
                timestamp = 0L,
                date= "2025-10-01",
                duration = 1L
            ),
            RecordedAudio(
                id = 2,
                filePath = "path2",
                timestamp = 0L,
                date= "2025-10-01",
                duration = 1L
            )
        )
        coEvery { audioRepositoryImpl.getAudiosForTask(taskId.toString()) } returns mockComments

        viewModel.loadCommentsByTaskId(taskId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockComments, viewModel.comments.first()) // TODO: Testear con Turbine??
    }

    @Test
    fun `playAudio should start playing new audio`() = runTest {
        val filePath = "testPath"
        coEvery { audioPlayerManager.play(filePath, any()) } just Runs

        viewModel.playAudio(filePath)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(filePath, viewModel.currentlyPlayingPath.first())
        assertTrue(viewModel.isPlaying.first())
    }

    @Test
    fun `stopAudio should stop playing audio`() = runTest {
        viewModel.stopAudio()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.currentlyPlayingPath.first())
        assertEquals(0L, viewModel.currentPosition.first())
        assertFalse(viewModel.isPlaying.first())
    }

    @Test
    fun `deleteComment should remove audio from repository and update state`() = runTest {
        val filePath = "testPath"
        viewModel.loadCommentsByTaskId(1)

        coEvery { audioRepositoryImpl.deleteAudio(filePath) } just Runs

        viewModel.deleteComment(filePath)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse(viewModel.comments.first().any { it.filePath == filePath })
    }

    @Test
    fun `confirmEditName should update audio name`() = runTest {
        val filePath = "testPath"
        val newName = "newTitle"
        viewModel.openEditDialog(filePath, "oldTitle")
        viewModel.updateEditingName(newName)

        coEvery { audioRepositoryImpl.updateAudioName(filePath, newName) } just Runs

        viewModel.confirmEditName()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(newName, viewModel.comments.first().first { it.filePath == filePath }.title)
    }
}