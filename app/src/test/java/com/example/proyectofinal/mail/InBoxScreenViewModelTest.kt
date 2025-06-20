package com.example.proyectofinal.mail

import android.util.Log
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetInboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetOutboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.ReceiveMessageUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
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

    private val getInboxMessagesUseCase: GetInboxMessagesUseCase = mockk()
    private val getOutboxMessagesUseCase: GetOutboxMessagesUseCase = mockk()
    private val getDraftMessagesUseCase: GetDraftMessagesUseCase = mockk()
    private val deleteDraftUseCase: DeleteMessageByIdUseCase = mockk()
    private val receiveMessageUseCase: ReceiveMessageUseCase = mockk()
    private val tokenManager: TokenManager = mockk()

    private lateinit var viewModel: InboxViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val inboxMessagesStub = listOf(
        MessageModel(
            1, "userFromId", "studentId", false, "sender", "Subject",
            "2025-06-01", "Content of the inbox message", isResponse = false
        )
    )
    private val outboxMessagesStub = listOf(
        MessageModel(
            2, "userFromId", "studentId", false, "sender", "Subject",
            "2025-06-01", "Content of the outbox message", isResponse = false
        )
    )
    private val draftMessagesStub = listOf(
        MessageModel(
            3, "userFromId", "studentId", true, "sender", "Subject",
            "2025-06-01", "Content of the draft message", isResponse = false
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        coEvery { getInboxMessagesUseCase(any()) } returns inboxMessagesStub
        coEvery { getOutboxMessagesUseCase(any()) } returns outboxMessagesStub
        coEvery { getDraftMessagesUseCase() } returns draftMessagesStub
        coEvery { deleteDraftUseCase(any()) } returns Unit
        coEvery { receiveMessageUseCase(any()) } returns flowOf(
            NetworkResponse.Success(listOf())
        )
        coEvery { tokenManager.token } returns flowOf(null)
        coEvery { tokenManager.saveToken(any()) } just Runs
        coEvery { tokenManager.userId } returns flowOf("example_id")
        coEvery { tokenManager.saveUserId(any()) } just Runs
        coEvery { tokenManager.saveUser(any()) } just Runs
        viewModel = InboxViewModel(
            getInboxMessagesUseCase,
            getOutboxMessagesUseCase,
            getDraftMessagesUseCase,
            deleteDraftUseCase,
            receiveMessageUseCase,
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

        assertEquals(inboxMessagesStub, viewModel.inboxMessages.first())
        assertEquals(outboxMessagesStub, viewModel.outboxMessages.first())
        assertEquals(draftMessagesStub, viewModel.draftMessages.first())
    }

    @Test
    fun `discardDraft should delete draft and reload drafts`() = testScope.runTest {
        val draftId = 3

        viewModel.discardDraft(draftId)

        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { deleteDraftUseCase(draftId) }
        coVerify { getDraftMessagesUseCase() }
        assertEquals(draftMessagesStub, viewModel.draftMessages.first())
    }

}