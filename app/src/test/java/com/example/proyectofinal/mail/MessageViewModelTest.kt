package com.example.proyectofinal.mail

import com.example.proyectofinal.mail.domain.model.MessageModel
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftByIdUseCase
import com.example.proyectofinal.mail.domain.usecase.SaveDraftUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MessageViewModelTest {

    private val sendMessageUseCase: SendMessageUseCase = mockk(relaxed = true)
    private val saveDraftUseCase: SaveDraftUseCase = mockk(relaxed = true)
    private val getDraftByIdUseCase: GetDraftByIdUseCase = mockk(relaxed = true)
    private val deleteDraftUseCase: DeleteMessageByIdUseCase = mockk(relaxed = true)

    private lateinit var viewModel: MessageViewModel

    @Before
    fun setup() {
        viewModel = MessageViewModel(
            sendMessageUseCase = sendMessageUseCase,
            saveDraftUseCase = saveDraftUseCase,
            getDraftByIdUseCase = getDraftByIdUseCase,
            deleteDraftUseCase = deleteDraftUseCase
        )
    }

    @Test
    fun whenToIsUpdated_StateIsUpdated() = runTest {
        viewModel.updateTo("destino@correo.com")
//        delay(100)
        assertEquals("destino@correo.com", viewModel.to.value)
    }


    @Test
    fun whenSendingMessage_UseCaseIsExecuted() = runTest {
        // Arrange
        val expectedMessage = MessageModel(
            sender = "destino@correo.com",
            subject = "Asunto",
            content = "Mensaje",
            date = "",
            id = 0
        )

        viewModel.updateTo(expectedMessage.sender)
        viewModel.updateSubject(expectedMessage.subject)
        viewModel.updateMessage(expectedMessage.content)

        // Act
        viewModel.sendMessage(expectedMessage)

        // Assert
        coVerify {
            sendMessageUseCase(expectedMessage)
        }
    }


//    @Test
//    fun `cuando se guarda un borrador, se ejecuta el caso de uso de borrador`() = runTest {
//        viewModel.updateTo("destino@correo.com")
//        viewModel.updateSubject("Asunto")
//        viewModel.updateMessage("Mensaje")
//
//        viewModel.saveDraft()
//
//        coVerify {
//            saveDraftUseCase(
//                message = "Mensaje"
//            )
//        }
//    }
}
