package com.example.proyectofinal.mail

import com.example.proyectofinal.mail.domain.model.MessageModel
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
    private lateinit var viewModel: MessageViewModel

    @Before
    fun setup() {
        viewModel = MessageViewModel(
            sendMessageUseCase = sendMessageUseCase,
            //saveDraftUseCase = saveDraftUseCase
        )
    }

    @Test
    fun WhenUpdatingToField_StateFlowChanges() = runTest {
        viewModel.updateTo("destino@correo.com")
        delay(100)
        assertEquals("destino@correo.com", viewModel.to.value)
    }


    @Test
    fun WhenSendingMessage_UseCaseIsExecuted() = runTest {
        // Arrange
        val expectedMessage = MessageModel(
            sender = "destino@correo.com",
            subject = "Asunto",
            content = "Mensaje",
            date = ""
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
