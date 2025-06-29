package com.example.proyectofinal.mail

import android.util.Log
import com.example.proyectofinal.auth.data.model.UserResponseDto
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.data.local.MessageDao
import com.example.proyectofinal.mail.domain.model.MessageModel
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

@OptIn(ExperimentalCoroutinesApi::class)
class InBoxScreenViewModelTest {

    private val getDraftMessagesUseCase: GetDraftMessagesUseCase = mockk()
    private val deleteMessageByIdUseCase: DeleteMessageByIdUseCase = mockk()
    private val receiveOutboxMessagesUseCase: ReceiveOutboxMessageUseCase = mockk()
    private val receiveMessageUseCase: ReceiveMessageUseCase = mockk()
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
    private val userMock = User("1", "user1", "User One", "user1@example.com", "password", "1234567890", listOf("role1"))
    private val userResponseMock = UserResponseDto("user1", "user1@example.com", "User One", listOf("role1"))

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        coEvery { getDraftMessagesUseCase() } returns draftMessagesStub
        coEvery { deleteMessageByIdUseCase(any()) } returns Unit
        coEvery { receiveMessageUseCase(any()) } returns flowOf(
            NetworkResponse.Success(listOf())
        )
        coEvery { receiveOutboxMessagesUseCase(any()) } returns flowOf(
            NetworkResponse.Success(emptyList())
        )
        coEvery { tokenManager.token } returns flowOf(null)
        coEvery { tokenManager.saveToken(any()) } just Runs
        coEvery { tokenManager.userId } returns flowOf("example_id")
        coEvery { tokenManager.user } returns flowOf(userResponseMock)
        coEvery { tokenManager.saveUserId(any()) } just Runs
        coEvery { tokenManager.saveUser(any()) } just Runs

        coEvery { userProviderMock.getUsers() } returns flowOf(NetworkResponse.Success(listOf(userMock)))

        viewModel = InboxViewModel(
            getDraftMessagesUseCase,
            deleteMessageByIdUseCase,
            receiveMessageUseCase,
            receiveOutboxMessagesUseCase,
            messageDao,
            tokenManager
        )
    }

    @After
    fun tearDown() {
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

}