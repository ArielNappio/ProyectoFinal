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
}
