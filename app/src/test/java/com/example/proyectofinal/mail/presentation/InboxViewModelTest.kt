package com.example.proyectofinal.mail.presentation

import android.util.Log
import app.cash.turbine.test
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.data.local.MessageDao
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.ReceiveMessageUseCase
import com.example.proyectofinal.mail.domain.usecase.ReceiveOutboxMessageUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.data.provider.UserProviderImpl
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class InboxViewModelTest {

    private lateinit var getDraftMessagesUseCase: GetDraftMessagesUseCase
    private lateinit var deleteMessageByIdUseCase: DeleteMessageByIdUseCase
    private lateinit var receiveOutboxMessagesUseCase: ReceiveOutboxMessageUseCase
    private lateinit var receiveMessageUseCase: ReceiveMessageUseCase
    private lateinit var mailRepository: MailRepository
    private val messageDao: MessageDao = mockk(relaxed = true)
    private val tokenManager: TokenManager = mockk()
    private val userProviderMock: UserProviderImpl = mockk()

    private lateinit var viewModel: InboxViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val draftMessagesStub = listOf(
        MessageModel(
            3, "userFromId", "1", true, "sender", "Subject",
            "2025-06-01", "Content of the draft message", isResponse = false
        )
    )
    private val userMock = User(
        "1",
        "user1",
        "User One",
        "user1@example.com",
        "password",
        "1234567890",
        listOf("role1")
    )
    private val userResponseMock =
        UserResponseDto("user1", "user1@example.com", "User One", listOf("role1"))

    @Before
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.w(any<String>(), any<String>()) } returns 0
        every { Log.e(any(), any()) } returns 0

        Dispatchers.setMain(testDispatcher)

        mailRepository = mockk(relaxed = true)

        coEvery { mailRepository.getDrafts() } returns draftMessagesStub
        coEvery { mailRepository.deleteDraftById(any()) } returns Unit
        coEvery { mailRepository.receiveMessages(any()) } returns flowOf(
            NetworkResponse.Success(listOf())
        )
        coEvery { mailRepository.receiveOutboxMessages(any()) } returns flowOf(
            NetworkResponse.Success(emptyList())
        )
        coEvery { tokenManager.token } returns flowOf(null)
        coEvery { tokenManager.saveToken(any()) } just Runs
        coEvery { tokenManager.userId } returns flowOf("example_id")
        coEvery { tokenManager.user } returns flowOf(userResponseMock)
        coEvery { tokenManager.saveUserId(any()) } just Runs
        coEvery { tokenManager.saveUser(any()) } just Runs

        coEvery { userProviderMock.getUsers() } returns flowOf(
            NetworkResponse.Success(
                listOf(
                    userMock
                )
            )
        )

        getDraftMessagesUseCase = GetDraftMessagesUseCase(mailRepository)
        deleteMessageByIdUseCase = DeleteMessageByIdUseCase(mailRepository)
        receiveMessageUseCase = ReceiveMessageUseCase(mailRepository)
        receiveOutboxMessagesUseCase = ReceiveOutboxMessageUseCase(mailRepository)

        viewModel = InboxViewModel(
            getDraftMessagesUseCase,
            deleteMessageByIdUseCase,
            receiveMessageUseCase,
            receiveOutboxMessagesUseCase,
            messageDao,
            tokenManager
        )

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        testScope.cancel()
        Dispatchers.resetMain()
    }

    @Test
    fun `setCurrentUserId should load inbox, outbox, and draft messages`() = testScope.runTest {
        val userId = "1"

        viewModel.setCurrentUserId(userId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(emptyList<MessageModel>(), viewModel.inboxMessages.first())
        assertEquals(emptyList<MessageModel>(), viewModel.outboxMessages.first())
        assertEquals(draftMessagesStub, viewModel.draftMessages.first())
    }

    @Test
    fun `discardDraft should delete draft and reload drafts`() = testScope.runTest {
        val draftId = 3

        viewModel.discardDraft(draftId)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { deleteMessageByIdUseCase(draftId) }
        coVerify { getDraftMessagesUseCase() }
        assertEquals(draftMessagesStub, viewModel.draftMessages.first())
    }

    @Test
    fun `getMessageById should return message from drafts`() = testScope.runTest {
        val result = viewModel.getMessageById(draftMessagesStub.first().id)
        assertEquals(draftMessagesStub.first(), result)
    }

    @Test
    fun `getMessageById should return null if message not found`() = testScope.runTest {
        val invalidId = 999
        val result = viewModel.getMessageById(invalidId)
        assertNull(result)
    }

    @Test
    fun `when external provider error should load inbox, outbox, and draft messages`() =
        testScope.runTest {
            coEvery { mailRepository.receiveMessages(any()) } returns flowOf(
                NetworkResponse.Failure("unknown error")
            )
            coEvery { mailRepository.receiveOutboxMessages(any()) } returns flowOf(
                NetworkResponse.Failure("unknown error")
            )

            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(emptyList<MessageModel>(), viewModel.inboxMessages.first())
            assertEquals(emptyList<MessageModel>(), viewModel.outboxMessages.first())
        }

    @Test
    fun `messages are sorted correctly by date`() = testScope.runTest {
        val message1 = MessageModel(
            id = 1,
            sender = "user1@example.com",
            date = "2025-06-22T22:00:43.164Z",
            userFromId = "1",
            userToId = "2",
            isDraft = false,
            subject = "subject",
            content = "content"
        )
        val message2 = MessageModel(
            id = 2,
            sender = "user1@example.com",
            date = "2025-06-23T22:00:43.164Z",
            userFromId = "1",
            userToId = "2",
            isDraft = false,
            subject = "subject",
            content = "content"
        )
        val message3 = MessageModel(
            id = 3,
            sender = "user1@example.com",
            date = "2025-06-21T22:00:43.164Z",
            userFromId = "1",
            userToId = "2",
            isDraft = false,
            subject = "subject",
            content = "content"
        )
        val unsortedMessages = listOf(message1, message2, message3)
        val sortedMessages = listOf(message2, message1, message3)
        val sortedMessagesFormatedDates = listOf("Mon 23 Jun 2025 22:00", "Sun 22 Jun 2025 22:00", "Sat 21 Jun 2025 22:00")
        coEvery { mailRepository.receiveMessages(any()) } returns flowOf(
            NetworkResponse.Success(unsortedMessages)
        )
        viewModel = InboxViewModel(
            getDraftMessagesUseCase,
            deleteMessageByIdUseCase,
            receiveMessageUseCase,
            receiveOutboxMessagesUseCase,
            messageDao,
            tokenManager
        )

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mailRepository.receiveMessages(any()) }
        val expectedResult = sortedMessages.mapIndexed { index, message ->
            message.copy(date = sortedMessagesFormatedDates[index])
        }
        viewModel.inboxMessages.test {
            var result = awaitItem()
            while (result.isEmpty()) {
                result = awaitItem()
                assertEquals(expectedResult, result)
            }
            cancelAndConsumeRemainingEvents()
        }
    }

}