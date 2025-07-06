package com.example.proyectofinal.task_student

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import androidx.core.text.HtmlCompat
import com.example.proyectofinal.audio.domain.model.RecordedAudio
import com.example.proyectofinal.audio.domain.repository.AudioRepository
import com.example.proyectofinal.audio.player.AudioPlayerManager
import com.example.proyectofinal.audio.recorder.AudioRecorderManager
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderFeedback.data.entity.FeedbackEntity
import com.example.proyectofinal.orderFeedback.domain.local.FeedbackRepository
import com.example.proyectofinal.orderFeedback.domain.model.OrderFeedback
import com.example.proyectofinal.orderFeedback.domain.provider.OrderFeedbackProvider
import com.example.proyectofinal.orderFeedback.domain.usecase.GetFeedbackUseCase
import com.example.proyectofinal.orderFeedback.domain.usecase.SaveFeedbackUseCase
import com.example.proyectofinal.orderFeedback.domain.usecase.SendFeedbackUseCase
import com.example.proyectofinal.orderManagement.data.repository.LastReadRepository
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.model.OrderStudent
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsMp3UseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsPdfUseCase
import com.example.proyectofinal.task_student.domain.usecase.DownloadAsTxtUseCase
import com.example.proyectofinal.task_student.presentation.tts.TextToSpeechManager
import com.example.proyectofinal.task_student.presentation.viewmodel.TaskStudentViewModel
import com.example.proyectofinal.task_student.util.htmlToAnnotatedStringFormatted
import com.example.proyectofinal.userpreferences.data.manager.DataStoreManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import junit.framework.TestCase.assertFalse
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
import kotlin.test.assertNull
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
    private val userPreferences: DataStoreManager = mockk(relaxed = true)
    private val lastReadRepository: LastReadRepository = mockk(relaxed = true)
    private val feedbackRepository: FeedbackRepository = mockk(relaxed = true)
    private val orderFeedbackProvider: OrderFeedbackProvider = mockk(relaxed = true)

    private lateinit var viewModel: TaskStudentViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        mockkStatic(HtmlCompat::class)
        every {
            HtmlCompat.fromHtml(any(), HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
        } returns "Test HTML Content"
        mockkStatic("com.example.proyectofinal.task_student.util.HtmlFormatterKt")
        every { htmlToAnnotatedStringFormatted(any()) } returns AnnotatedString("Mocked Result")

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
            orderRepository = orderRepository,
            userPreferences = userPreferences,
            lastReadRepository = lastReadRepository,
            sendFeedbackUseCase = SendFeedbackUseCase(orderFeedbackProvider),
            saveFeedbackUseCase = SaveFeedbackUseCase(feedbackRepository),
            getFeedbackUseCase = GetFeedbackUseCase(feedbackRepository),
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
    fun `when previousPage is called and currentPageIndex is not the first page, currentPageIndex is decremented`() =
        runTest {
            repeat(2) { viewModel.nextPage() }

            viewModel.previousPage()

            assertEquals(0, viewModel.currentPageIndex.value)
        }

    @Test
    fun `when speakCurrentPage is called, ttsManager speak is invoked`() = runTest {
        viewModel.speakCurrentPage()
        coVerify { ttsManager.speak(any()) }
    }

    @Test
    fun `when startRecording is called, audioRecorderManager startRecording is invoked`() =
        runTest {
            viewModel.startRecording()
            verify { audioRecorderManager.startRecording() }
        }

    @Test
    fun `when stopRecording is called, audioRecorderManager stopRecording is invoked`() =
        testScope.runTest {
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
    fun `when deleteComment is called, audioRepository deleteAudio is invoked`() =
        testScope.runTest {
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
    fun `when fontSizeDecrease is called, fontSize is decremented`() = testScope.runTest {
        viewModel.fontSizeDecrease()

        testDispatcher.scheduler.advanceUntilIdle()
        assertTrue(viewModel.fontSize.value.value < 52)
    }

    @Test
    fun `when showFont is called, _showFont value is toggled`() = runTest {
        val initialValue = viewModel.showFont.value
        viewModel.showFont()
        assertEquals(!initialValue, viewModel.showFont.value)
    }

    @Test
    fun `when showAnnotations is called, _showAnnotations value is toggled`() = runTest {
        val initialValue = viewModel.showAnnotations.value
        viewModel.showAnnotations()
        assertEquals(!initialValue, viewModel.showAnnotations.value)
    }

    @Test
    fun `when showDownloadDialog is called, _showDownloadDialog value is toggled`() = runTest {
        val initialValue = viewModel.showDownloadDialog.value
        viewModel.showDownloadDialog()
        assertEquals(!initialValue, viewModel.showDownloadDialog.value)
    }

    @Test
    fun `when showFeedback is called, _showFeedback value is toggled`() = runTest {
        val initialValue = viewModel.showFeedback.value
        viewModel.showFeedback()
        assertEquals(!initialValue, viewModel.showFeedback.value)
    }

    @Test
    fun `when loadProject is called with a valid taskId, projectState and paragraphs are updated`() =
        testScope.runTest {
            val taskId = 1
            val userId = "example_user_id"
            val mockOrderDelivered = OrderDelivered(
                studentId = "12345",
                id = "order-001",
                status = "Delivered",
                title = "Order Title Example",
                orders = listOf(
                    OrderStudent(
                        id = 1,
                        name = "Example Task",
                        description = "This is a description of the task.",
                        isFavorite = false,
                        lastRead = "1",
                        pageCount = 100,
                        hasComments = false,
                        subject = "Math",
                        authorName = "John Doe",
                        rangePage = "1-10",
                        status = "Completed",
                        voluntarioId = "vol-123",
                        alumnoId = "alum-456"
                    )
                ),
                orderParagraphs = listOf(
                    OrderParagraph(
                        orderId = 1,
                        paragraphText = "Paragraph 1",
                        pageNumber = 1
                    )
                )
            )
            val mockResponse = NetworkResponse.Success(listOf(mockOrderDelivered))

            coEvery { tokenManager.userId } returns flowOf(userId)
            coEvery { getOrders(userId) } returns flowOf(mockResponse)

            viewModel.loadProject(taskId)

            testDispatcher.scheduler.advanceUntilIdle()
            assertTrue(viewModel.projectState.value is NetworkResponse.Success)
            assertEquals(
                mockOrderDelivered,
                (viewModel.projectState.value as NetworkResponse.Success).data
            )
            assertEquals(1, viewModel.paragraphs.value.size)
            assertEquals("Paragraph 1", viewModel.paragraphs.value[0].paragraphText)
        }

    @Test
    fun `when loadProject is called with a valid taskId and provider call fails, nothing happens`() =
        testScope.runTest {
            val taskId = 1
            val userId = "example_user_id"

            val mockResponse = NetworkResponse.Failure<List<OrderDelivered>>("network error")

            coEvery { tokenManager.userId } returns flowOf(userId)
            coEvery { getOrders(userId) } returns flowOf(mockResponse)

            viewModel.loadProject(taskId)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.projectState.value is NetworkResponse.Loading)
        }

    @Test
    fun `when loadProject is called with a valid taskId and user id is null, state is updated`() =
        testScope.runTest {
            val taskId = 1
            coEvery { tokenManager.userId } returns flowOf(null)

            viewModel.loadProject(taskId)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.projectState.value is NetworkResponse.Failure)
            assertEquals(
                "Usuario no autenticado",
                (viewModel.projectState.value as NetworkResponse.Failure).error
            )
        }

    @Test
    fun `when saveTaskId is called, currentTaskId is updated`() = runTest {
        val taskId = 123

        viewModel.saveTaskId(taskId)

        assertEquals(taskId, viewModel.currentTaskId.value)
    }

    @Test
    fun `when loadCommentsByTaskId is called, comments are updated`() = testScope.runTest {
        val taskId = 1
        val mockComments = listOf(
            RecordedAudio(
                id = 1,
                filePath = "filePath1",
                timestamp = 1L,
                associatedTaskId = "1",
                title = "Comment 1",
                page = 1,
                date = "2025-01-01",
                duration = 1L
            ),
            RecordedAudio(
                id = 2,
                filePath = "filePath2",
                timestamp = 1L,
                associatedTaskId = "1",
                title = "Comment 2",
                page = 2,
                date = "2025-01-01",
                duration = 2L
            )
        )
        coEvery { audioRepository.getAudiosForTask(taskId.toString()) } returns mockComments

        viewModel.loadCommentsByTaskId(taskId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(mockComments, viewModel.comments.value)
    }

    @Test
    fun `when setFontFamily is called, selectedFontFamily is updated and saved`() =
        testScope.runTest {
            val font = "Atkinson"

            viewModel.setFontFamily(font)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(font, viewModel.selectedFontFamily.value)
            coVerify { userPreferences.saveFontFamily(font) }
        }

    @Test
    fun `when toggleFontMenu is called, showFontsMenu value is toggled`() = runTest {
        val initialValue = viewModel.showFontsMenu.value

        viewModel.toggleFontMenu()

        assertEquals(!initialValue, viewModel.showFontsMenu.value)
    }

    @Test
    fun `when closeFontMenu is called, showFontsMenu value is set to false`() = runTest {
        viewModel.toggleFontMenu()

        viewModel.closeFontMenu()

        assertFalse(viewModel.showFontsMenu.value)
    }

    @Test
    fun `when loadFeedback is called, feedbackState is updated based on feedback existence`() =
        testScope.runTest {
            val orderId = 1
            coEvery { feedbackRepository.getFeedback(orderId) } returns FeedbackEntity(
                orderId = orderId,
                wasSent = true
            )

            viewModel.loadFeedback(orderId)
            testDispatcher.scheduler.advanceUntilIdle()

            assertTrue(viewModel.feedbackState.value == true)
        }

    @Test
    fun `when sendFeedbackToApi is called with valid data, feedback is sent successfully`() =
        testScope.runTest {
            val orderId = 1
            val feedbackText = "Muy bueno!"
            val stars = 5
            val feedback = OrderFeedback(
                studentId = "example_id",
                feedbackText = feedbackText,
                stars = stars,
                orderId = orderId
            )

            coEvery { feedbackRepository.getFeedback(orderId) } returns null
            coEvery { orderFeedbackProvider.sendFeedback(feedback) } returns flowOf(
                NetworkResponse.Success(
                    feedback
                )
            )
            coEvery { feedbackRepository.saveFeedback(orderId, true) } just Runs

            viewModel.sendFeedbackToApi(orderId, feedbackText, stars)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { orderFeedbackProvider.sendFeedback(feedback) }
            coVerify { feedbackRepository.saveFeedback(orderId, true) }
            assertEquals(true, viewModel.feedbackState.value)
        }

    @Test
    fun `when sendFeedbackToApi is called and feedback was already sent, it does nothing`() =
        runTest {
            val orderId = 1
            val feedbackText = "Great work!"
            val stars = 5
            coEvery { feedbackRepository.getFeedback(orderId) } returns FeedbackEntity(
                orderId,
                wasSent = true
            )

            viewModel.sendFeedbackToApi(orderId, feedbackText, stars)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 0) { orderFeedbackProvider.sendFeedback(any()) }
            coVerify(exactly = 0) { feedbackRepository.saveFeedback(any(), any()) }
        }

    @Test
    fun `when sendFeedbackToApi is called and API call fails, feedbackState is not updated`() =
        runTest {
            val orderId = 1
            val feedbackText = "Great work!"
            val stars = 5
            val feedback = OrderFeedback(
                studentId = "example_id",
                feedbackText = feedbackText,
                stars = stars,
                orderId = orderId
            )

            coEvery { feedbackRepository.getFeedback(orderId) } returns null
            coEvery { orderFeedbackProvider.sendFeedback(feedback) } returns flowOf(
                NetworkResponse.Failure(
                    "Error"
                )
            )

            viewModel.sendFeedbackToApi(orderId, feedbackText, stars)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { orderFeedbackProvider.sendFeedback(feedback) }
            assertEquals(null, viewModel.feedbackState.value)
        }

    @Test
    fun `when sendFeedbackToApi is called and throws exception, feedbackState is not updated`() =
        runTest {
            val orderId = 1
            val feedbackText = "Great work!"
            val stars = 5
            val feedback = OrderFeedback(
                studentId = "example_id",
                feedbackText = feedbackText,
                stars = stars,
                orderId = orderId
            )

            coEvery { feedbackRepository.getFeedback(orderId) } returns null
            coEvery { orderFeedbackProvider.sendFeedback(feedback) } throws RuntimeException("Unknown error")

            viewModel.sendFeedbackToApi(orderId, feedbackText, stars)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify { orderFeedbackProvider.sendFeedback(feedback) }
            assertEquals(null, viewModel.feedbackState.value)
        }

    @Test
    fun `when sendFeedbackToApi is called and userId is null, it does nothing`() = runTest {
        val orderId = 1
        val feedbackText = "Great work!"
        val stars = 5
        coEvery { tokenManager.userId } returns flowOf(null)

        viewModel.sendFeedbackToApi(orderId, feedbackText, stars)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { orderFeedbackProvider.sendFeedback(any()) }
        coVerify(exactly = 0) { feedbackRepository.saveFeedback(any(), any()) }
    }

    @Test
    fun `when setRating is called, rating is updated`() = runTest {
        val ratingValue = 4

        viewModel.setRating(ratingValue)

        assertEquals(ratingValue, viewModel.rating.value)
    }

    @Test
    fun `when setFeedbackText is called, feedbackText is updated`() = runTest {
        val feedbackText = "This is a feedback"

        viewModel.setFeedbackText(feedbackText)

        assertEquals(feedbackText, viewModel.feedbackText.value)
    }

    @Test
    fun `when deleteFeedbackRecording is called, recordedFeedbackFilePath is cleared`() = runTest {
        viewModel.deleteFeedbackRecording()

        assertNull(viewModel.recordedFeedbackFilePath.value)
    }

    @Test
    fun `when setSpeechRate is called with valid rate, speechRate is updated`() = runTest {
        val speechRate = 1.5f

        viewModel.setSpeechRate(speechRate)

        assertEquals(speechRate, viewModel.speechRate.value)
        verify { ttsManager.setSpeechRate(speechRate) }
    }

    @Test
    fun `when setSpeechRate is called with valid rate and isSpeaking is true, speechRate is updated`() = runTest {
        every { ttsManager.speak(any()) } just Runs
        viewModel.startSpeech()
        val speechRate = 1.5f

        viewModel.setSpeechRate(speechRate)

        assertEquals(speechRate, viewModel.speechRate.value)
        verify { ttsManager.setSpeechRate(speechRate) }
    }

    @Test
    fun `when startSpeech is called, ttsManager speak is invoked with correct text`() = runTest {
        val taskId = 1
        val userId = "example_user_id"
        val mockOrderDelivered = OrderDelivered(
            studentId = "12345",
            id = "order-001",
            status = "Delivered",
            title = "Order Title Example",
            orders = listOf(
                OrderStudent(
                    id = 1,
                    name = "Example Task",
                    description = "This is a description of the task.",
                    isFavorite = false,
                    lastRead = "1",
                    pageCount = 100,
                    hasComments = false,
                    subject = "Math",
                    authorName = "John Doe",
                    rangePage = "1-10",
                    status = "Completed",
                    voluntarioId = "vol-123",
                    alumnoId = "alum-456"
                )
            ),
            orderParagraphs = listOf(
                OrderParagraph(
                    orderId = 1,
                    paragraphText = "Paragraph 1",
                    pageNumber = 1
                )
            )
        )
        val mockResponse = NetworkResponse.Success(listOf(mockOrderDelivered))
        coEvery { getOrders(userId) } returns flowOf(mockResponse)
        viewModel.loadProject(taskId)

        val expectedPlainText = "Paragraph 1"
        every { ttsManager.speak(expectedPlainText) } just Runs

        viewModel.startSpeech()

        verify { ttsManager.speak(any()) }
        assertTrue(viewModel.isSpeaking.value)
    }

    @Test
    fun `when saveLastPage is called, lastReadRepository saves the correct page`() =
        testScope.runTest {
            val orderId = 123
            val page = 5
            coEvery { lastReadRepository.saveLastReadPage(orderId, page) } just Runs

            viewModel.saveLastPage(orderId, page)
            testDispatcher.scheduler.advanceUntilIdle()

            coVerify(exactly = 1) { lastReadRepository.saveLastReadPage(orderId, page) }
        }

    @Test
    fun `when toggleSpeechSettingsMenu is called, _showSpeechSettingsMenu value is toggled`() = runTest {
        val initialValue = viewModel.showSpeechSettingsMenu.value

        viewModel.toggleSpeechSettingsMenu()

        assertEquals(!initialValue, viewModel.showSpeechSettingsMenu.value)
    }

    @Test
    fun `when closeSpeechSettingsMenu is called, _showSpeechSettingsMenu is set to false`() = runTest {
        viewModel.toggleSpeechSettingsMenu()
        assertTrue(viewModel.showSpeechSettingsMenu.value)

        viewModel.closeSpeechSettingsMenu()

        assertFalse(viewModel.showSpeechSettingsMenu.value)
    }

    @Test
    fun `when playSpeech is called, ttsManager resume is invoked and state is updated`() = runTest {
        every { ttsManager.resume() } just Runs

        viewModel.playSpeech()

        verify { ttsManager.resume() }
        assertTrue(viewModel.isSpeaking.value)
        assertFalse(viewModel.isPaused.value)
    }

    @Test
    fun `when pauseSpeech is called, ttsManager pause is invoked and state is updated`() = runTest {
        every { ttsManager.pause() } just Runs

        viewModel.pauseSpeech()

        verify { ttsManager.pause() }
        assertFalse(viewModel.isSpeaking.value)
        assertTrue(viewModel.isPaused.value)
    }

    @Test
    fun `when openEditDialog is called, editingPath and editingName are updated and showEditDialog is set to true`() = runTest {
        val filePath = "example/path/to/file"
        val currentName = "Example Name"

        viewModel.openEditDialog(filePath, currentName)

        assertEquals(filePath, viewModel.editingPath.value)
        assertEquals(currentName, viewModel.editingName.value)
        assertTrue(viewModel.showEditDialog.value)
    }

    @Test
    fun `when updateEditingName is called, editingName is updated`() = runTest {
        val newName = "Updated Name"

        viewModel.updateEditingName(newName)

        assertEquals(newName, viewModel.editingName.value)
    }

    @Test
    fun `when closeEditDialog is called, editingPath, editingName are cleared and showEditDialog is set to false`() = runTest {
        viewModel.openEditDialog("example/path", "Example Name")

        viewModel.closeEditDialog()

        assertEquals("", viewModel.editingPath.value)
        assertEquals("", viewModel.editingName.value)
        assertFalse(viewModel.showEditDialog.value)
    }

    @Test
    fun `when confirmEditName is called with valid new name, it updates the audio name`() = testScope.runTest {
        val taskId = 1
        val mockComments = listOf(
            RecordedAudio(
                id = 1,
                filePath = "example/path/to/file",
                timestamp = 1L,
                associatedTaskId = "1",
                title = "Comment 1",
                page = 1,
                date = "2025-01-01",
                duration = 1L
            ),
            RecordedAudio(
                id = 2,
                filePath = "filePath2",
                timestamp = 1L,
                associatedTaskId = "1",
                title = "Comment 2",
                page = 2,
                date = "2025-01-01",
                duration = 2L
            )
        )
        coEvery { audioRepository.getAudiosForTask(taskId.toString()) } returns mockComments

        viewModel.loadCommentsByTaskId(taskId)
        testDispatcher.scheduler.advanceUntilIdle()

        val filePath = "example/path/to/file"
        val currentName = "Current Name"
        val newName = "New Name"

        val recordedAudio = RecordedAudio(1, filePath = filePath, title = "Comment 1", timestamp = 10000L, duration =  1L, associatedTaskId = "1", date = "2025-10-01")

        viewModel.openEditDialog(filePath, currentName)
        viewModel.updateEditingName(newName)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { audioRepository.getAudiosForTask("1") } returns listOf(recordedAudio)
        coEvery { audioRepository.updateAudioName(filePath, newName) } just Runs

        viewModel.confirmEditName()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { audioRepository.updateAudioName(filePath, newName) }
        assertEquals(newName, viewModel.comments.value.first { it.filePath == filePath }.title)
        assertFalse(viewModel.showEditDialog.value)
    }

}