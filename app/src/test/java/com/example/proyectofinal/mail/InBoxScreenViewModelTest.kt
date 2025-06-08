package com.example.proyectofinal.mail

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetInboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetOutboxMessagesUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.Date
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class InBoxScreenViewModelTest {

    private val getInboxMessagesUseCase: GetInboxMessagesUseCase = mockk()
    private val getOutboxMessagesUseCase: GetOutboxMessagesUseCase = mockk()
    private val getDraftMessagesUseCase: GetDraftMessagesUseCase = mockk()
    private val deleteDraftUseCase: DeleteMessageByIdUseCase = mockk()

    private lateinit var viewModel: InboxViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val inboxMessagesStub = listOf(
        MessageModel(
            1, "userFromId", "studentId", false, "sender", "Subject",
            Date(), "Content of the inbox message", isResponse = false
        )
    )
    private val outboxMessagesStub = listOf(
        MessageModel(
            2, "userFromId", "studentId", false, "sender", "Subject",
            Date(), "Content of the outbox message", isResponse = false
        )
    )
    private val draftMessagesStub = listOf(
        MessageModel(
            3, "userFromId", "studentId", true, "sender", "Subject",
            Date(), "Content of the draft message", isResponse = false
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getInboxMessagesUseCase(any()) } returns inboxMessagesStub
        coEvery { getOutboxMessagesUseCase(any()) } returns outboxMessagesStub
        coEvery { getDraftMessagesUseCase() } returns draftMessagesStub
        coEvery { deleteDraftUseCase(any()) } returns Unit
        viewModel = InboxViewModel(
            getInboxMessagesUseCase,
            getOutboxMessagesUseCase,
            getDraftMessagesUseCase,
            deleteDraftUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setCurrentUserId should load inbox, outbox, and draft messages`() = testScope.runTest {
        val userId = 1

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

    @Test
    fun `loadInboxMessages should update inboxMessages state`() = testScope.runTest {
        val userId = 1

        viewModel.setCurrentUserId(userId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(inboxMessagesStub, viewModel.inboxMessages.first())
    }

    @Test
    fun `loadOutboxMessages should update outboxMessages state`() = testScope.runTest {
        val userId = 1

        viewModel.setCurrentUserId(userId)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(outboxMessagesStub, viewModel.outboxMessages.first())
    }

    @Test
    fun `loadDraftMessages should update draftMessages state`() = testScope.runTest {
        viewModel.setCurrentUserId(1)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(draftMessagesStub, viewModel.draftMessages.first())
    }
}