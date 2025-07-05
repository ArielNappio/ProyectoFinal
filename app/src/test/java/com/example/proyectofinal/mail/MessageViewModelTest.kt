package com.example.proyectofinal.mail

import android.util.Log
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.provider.MailProvider
import com.example.proyectofinal.mail.domain.repository.MailRepository
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.SaveDraftUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.domain.provider.UserProvider
import com.example.proyectofinal.users.domain.usecase.GetUserUseCase
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MessageViewModelTest {

    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var saveDraftUseCase: SaveDraftUseCase
    private lateinit var getDraftByIdUseCase: GetDraftByIdUseCase
    private lateinit var deleteDraftUseCase: DeleteMessageByIdUseCase
    private lateinit var getUserUseCase: GetUserUseCase
    private lateinit var mailRepository: MailRepository
    private lateinit var mailProvider: MailProvider
    private lateinit var userProvider: UserProvider
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var viewModel: MessageViewModel
    private val tokenManager: TokenManager = mockk()

    private val messageModelStub = MessageModel(
        1, "userFromId", "studentId", false, "sender", "Subject",
        "2025-06-01", "Content of the inbox message", isResponse = false
    )

    private val librarianUser = User(
        id = "1",
        userName = "userName",
        fullName = "fullName",
        email = "test@example.com",
        phoneNumber = "1234567890",
        roles = listOf("Bibliotecario")
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        mailRepository = mockk()
        coEvery { mailRepository.saveDraft(any()) } returns Unit
        coEvery { mailRepository.getDraftById(any()) } returns messageModelStub
        coEvery { mailRepository.deleteDraftById(any()) } returns Unit
        mailProvider = mockk()
        coEvery { mailProvider.sendMessage(any()) } returns flowOf(NetworkResponse.Success(messageModelStub))

        userProvider = mockk()
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        coEvery { tokenManager.token } returns flowOf(null)
        coEvery { tokenManager.saveToken(any()) } just Runs
        coEvery { tokenManager.saveUserId(any()) } just Runs
        coEvery { tokenManager.userId } returns flowOf("example_id")
        coEvery { tokenManager.saveUser(any()) } just Runs

        sendMessageUseCase = SendMessageUseCase(mailProvider)
        saveDraftUseCase = SaveDraftUseCase(mailRepository)
        getDraftByIdUseCase = GetDraftByIdUseCase(mailRepository)
        deleteDraftUseCase = DeleteMessageByIdUseCase(mailRepository)
        getUserUseCase = GetUserUseCase(userProvider)

        viewModel = MessageViewModel(
            sendMessageUseCase = sendMessageUseCase,
            saveDraftUseCase = saveDraftUseCase,
            getDraftByIdUseCase = getDraftByIdUseCase,
            deleteDraftUseCase = deleteDraftUseCase,
            getUserUseCase = getUserUseCase,
            tokenManager = tokenManager
        )
    }

    @Test
    fun `when updating 'to', state is updated`() = runTest {
        viewModel.updateTo("test@example.com")
        assertEquals("test@example.com", viewModel.to.value)
    }

    @Test
    fun `when updating 'subject', state is updated`() = runTest {
        viewModel.updateSubject("Test Subject")
        assertEquals("Test Subject", viewModel.subject.value)
    }

    @Test
    fun `when updating 'message', state is updated`() = runTest {
        viewModel.updateMessage("Test Message")
        assertEquals("Test Message", viewModel.message.value)
    }

    @Test
    fun `when sending message, use case is executed`() = testScope.runTest {
        viewModel.updateTo("test@example.com")
        viewModel.updateSubject("Test Subject")
        viewModel.updateMessage("Test Message")
        viewModel.getLibrarianEmails()
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.sendMessage()
        testDispatcher.scheduler.advanceUntilIdle()


        coVerify { sendMessageUseCase(any()) }
    }

    @Test
    fun `when saving draft, use case is executed`() = testScope.runTest {
        viewModel.updateTo("test@example.com")
        viewModel.updateSubject("Test Subject")
        viewModel.updateMessage("Test Message")
        viewModel.saveDraft()

        testDispatcher.scheduler.advanceUntilIdle()
        coVerify { saveDraftUseCase(any()) }
    }

    @Test
    fun `when loading draft, state is updated`() = testScope.runTest {
        viewModel.loadDraft(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mailRepository.getDraftById(1) }
    }

    @Test
    fun `when discarding draft, use case is executed`() = runTest {
        viewModel.discardDraft(1)

        coVerify { deleteDraftUseCase(1) }
    }

    @Test
    fun `when removeAttachment is called, formPath and attachments are cleared`() = runTest {
        // Arrange
        viewModel.addAttachment("testFilePath")
        viewModel.updateFormPath("testFormPath")

        viewModel.removeAttachment()

        assertEquals(null, viewModel.formPath.value)
        assertEquals(emptyList<String>(), viewModel.attachments.value)
    }

    @Test
    fun `when addAttachment is called, filePath is added to attachments`() = runTest {
        val filePath = "testFilePath"

        viewModel.addAttachment(filePath)

        assertEquals(listOf(filePath), viewModel.attachments.value)
    }

    @Test
    fun `appendToMessage should append new text to the existing message`() = runTest {
        val initialMessage = "Hello"
        val newText = "World"
        viewModel.updateMessage(initialMessage)

        viewModel.appendToMessage(newText)

        val result = viewModel.message.first()
        assertEquals("Hello World", result)
    }

    @Test
    fun `when loadUserId is called, currentUserId is updated`() = testScope.runTest {
        coEvery { tokenManager.userId } returns flowOf("test_user_id")

        viewModel.loadUserId()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("test_user_id", viewModel.currentUserId.value)
    }

    @Test
    fun `when getUserIdByEmailSync is called with valid email, correct userId is returned`() = testScope.runTest {
        val email = "test@example.com"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        val userId = viewModel.getUserIdByEmailSync(email)

        assertEquals("1", userId)
    }

    @Test
    fun `when getUserIdByEmailSync is called with invalid email, empty userId is returned`() = testScope.runTest {
        val email = "invalid@example.com"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        val userId = viewModel.getUserIdByEmailSync(email)

        assertEquals("", userId)
    }

    @Test
    fun `when getUserIdByEmail is called with valid email, userToId is updated`() = testScope.runTest {
        val email = "test@example.com"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        viewModel.getUserIdByEmail(email)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("1", viewModel.userToId.value)
    }

    @Test
    fun `when getUserIdByEmail is called with invalid email, userToId is empty`() = testScope.runTest {
        val email = "invalid@example.com"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        viewModel.getUserIdByEmail(email)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.userToId.value)
    }

    @Test
    fun `when getEmailByUserIdSync is called with valid userId, correct email is returned`() = testScope.runTest {
        val userId = "1"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        val email = viewModel.getEmailByUserIdSync(userId)

        assertEquals("test@example.com", email)
    }

    @Test
    fun `when getEmailByUserIdSync is called with invalid userId, 'Desconocido' is returned`() = testScope.runTest {
        val userId = "invalid_id"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        val email = viewModel.getEmailByUserIdSync(userId)

        assertEquals("Desconocido", email)
    }

    @Test
    fun `when getEmailByUserId is called with valid userId, 'to' is updated`() = testScope.runTest {
        val userId = "1"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        viewModel.getEmailByUserId(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("test@example.com", viewModel.to.value)
    }

    @Test
    fun `when getEmailByUserId is called with invalid userId, 'to' is empty`() = testScope.runTest {
        val userId = "invalid_id"
        coEvery { userProvider.getUsers() } returns flowOf(NetworkResponse.Success(listOf(librarianUser)))

        viewModel.getEmailByUserId(userId)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals("", viewModel.to.value)
    }
}
