package com.example.proyectofinal.mail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.domain.provider.MailProvider
import com.example.proyectofinal.mail.domain.usecase.ReceiveConversationByIdUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.ConversationViewModel
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ConversationViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ConversationViewModel
    private lateinit var mailProvider: MailProvider
    private lateinit var receiveConversationByIdUseCase: ReceiveConversationByIdUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mailProvider = mockk()
        every { mailProvider.receiveConversationById(any()) } returns flow {
            emit(NetworkResponse.Loading())
        }

        receiveConversationByIdUseCase = ReceiveConversationByIdUseCase(mailProvider)
        viewModel = ConversationViewModel(receiveConversationByIdUseCase)
    }

    @Test
    fun `loadConversation should emit success state when use case returns data`() = runTest {
        val mockMessages = listOf(
            MessageModelDto(
                id = 1,
                userFromId = "user123",
                userToId = "user456",
                isDraft = false,
                sender = "example@example.com",
                subject = "Test Subject",
                date = "2023-10-01",
                content = "This is a test message content.",
                file = null,
                isResponse = false,
                responseText = null,
                isRead = true
            )
        )
        every { mailProvider.receiveConversationById(any()) } returns flow { emit(NetworkResponse.Success(mockMessages)) }
        val conversationId = "123"

        viewModel.loadConversation(conversationId)
        advanceUntilIdle()

        assert(viewModel.conversationState.first() is NetworkResponse.Success)
        assertEquals(mockMessages, viewModel.conversationState.first().data)
        verify { mailProvider.receiveConversationById(conversationId) }
    }

    @Test
    fun `loadConversation should emit failure state when use case returns error`() = runTest {
        val conversationId = "123"
        val errorMessage = "Error occurred"

        every { mailProvider.receiveConversationById(any()) } returns flow { emit(NetworkResponse.Failure(errorMessage)) }

        viewModel.loadConversation(conversationId)
        advanceUntilIdle()

        assert(viewModel.conversationState.first() is NetworkResponse.Failure)
        assertEquals(viewModel.conversationState.first().error, errorMessage)
        verify { mailProvider.receiveConversationById(conversationId) }
    }

    @Test
    fun `loadConversation should emit loading state initially`() = runTest {
        val conversationId = "123"
        val flowResponse = flow<NetworkResponse<List<MessageModelDto>>> { }

        coEvery { receiveConversationByIdUseCase(conversationId) } returns flowResponse

        viewModel.loadConversation(conversationId)

        assert(viewModel.conversationState.value is NetworkResponse.Loading)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}