package com.example.proyectofinal.mail

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.repository.MailRepository
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.SaveDraftUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MessageViewModelTest {

    private val sendMessageUseCase: SendMessageUseCase = mockk(relaxed = true)
    private lateinit var saveDraftUseCase: SaveDraftUseCase
    private lateinit var getDraftByIdUseCase: GetDraftByIdUseCase
    private lateinit var deleteDraftUseCase: DeleteMessageByIdUseCase
    private lateinit var mailRepository: MailRepository
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var viewModel: MessageViewModel

    private val messageModelStub = MessageModel(
        sender = "test@example.com",
        subject = "Test Subject",
        content = "Test Message",
        date = "2023-01-01",
        id = 1
    )

    @Before
    fun setup() {
        mailRepository = mockk()
        coEvery { mailRepository.saveDraft(any()) } returns Unit
        coEvery { mailRepository.getDraftById(any()) } returns messageModelStub
        coEvery { mailRepository.deleteDraftById(any()) } returns Unit

        saveDraftUseCase = SaveDraftUseCase(mailRepository)
        getDraftByIdUseCase = GetDraftByIdUseCase(mailRepository)
        deleteDraftUseCase = DeleteMessageByIdUseCase(mailRepository)

        viewModel = MessageViewModel(
            sendMessageUseCase = sendMessageUseCase,
            saveDraftUseCase = saveDraftUseCase,
            getDraftByIdUseCase = getDraftByIdUseCase,
            deleteDraftUseCase = deleteDraftUseCase
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
    fun `when sending message, use case is executed`() = runTest {
        viewModel.sendMessage(messageModelStub)

        coVerify { sendMessageUseCase(messageModelStub) }
    }

    @Test
    fun `when saving draft, use case is executed`() = testScope.runTest {
        viewModel.updateTo("test@example.com")
        viewModel.updateSubject("Test Subject")
        viewModel.updateMessage("Test Message")

        viewModel.saveDraft()

        coVerify { saveDraftUseCase(messageModelStub) }
    }

    @Test
    fun `when loading draft, state is updated`() = testScope.runTest {
        viewModel.loadDraft(1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { getDraftByIdUseCase(1) }

        assertEquals(messageModelStub.sender, viewModel.to.value)
        assertEquals(messageModelStub.subject, viewModel.subject.value)
        assertEquals(messageModelStub.content, viewModel.message.value)
    }

    @Test
    fun `when discarding draft, use case is executed`() = runTest {
        viewModel.discardDraft(1)

        coVerify { deleteDraftUseCase(1) }
    }
}
