package com.example.proyectofinal.mail.domain

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.mail.domain.model.MessageModelDto
import com.example.proyectofinal.mail.domain.provider.MailProvider
import com.example.proyectofinal.mail.domain.usecase.ReceiveAllConversationsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.collections.List

class ReceiveAllConversationsUseCaseTest {

    private lateinit var mailProvider: MailProvider
    private lateinit var receiveAllConversationsUseCase: ReceiveAllConversationsUseCase

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        mailProvider = mockk()
        receiveAllConversationsUseCase = ReceiveAllConversationsUseCase(mailProvider)
    }

    @Test
    fun `invoke should return success response with list of messages`() = testScope.runTest {
        // Arrange
        val messages = listOf(
            MessageModelDto(
                id = 1,
                userFromId = "user123",
                userToId = "user456",
                isDraft = false,
                sender = "example@example.com",
                subject = "Test Subject 1",
                date = "2025-10-01",
                content = "1 - This is a test message content.",
                file = null,
                isResponse = false,
                responseText = null,
                isRead = true
            ),
            MessageModelDto(
                id = 2,
                userFromId = "user123",
                userToId = "user456",
                isDraft = false,
                sender = "example@example.com",
                subject = "Test Subject 2",
                date = "2025-10-01",
                content = "2 - This is a test message content.",
                file = null,
                isResponse = false,
                responseText = null,
                isRead = true
            )
        )
        coEvery { mailProvider.receiveAllConversations() } returns flowOf(NetworkResponse.Success(messages))

        // Act
        val result = receiveAllConversationsUseCase().first()
        testDispatcher.scheduler.advanceUntilIdle()

        // Assert
        assert(result is NetworkResponse.Success)
        assertEquals(messages, result.data)
    }

    @Test
    fun `invoke should return failure response`() = runTest {
        // Arrange
        val errorMessage = "Error fetching conversations"
        coEvery { mailProvider.receiveAllConversations() } returns flowOf(NetworkResponse.Failure(errorMessage))

        // Act
        val result = receiveAllConversationsUseCase().first()

        // Assert
        assert(result is NetworkResponse.Failure<List<MessageModelDto>>)
        assertEquals(errorMessage, (result as NetworkResponse.Failure).error)
    }

    @Test
    fun `invoke should return loading response`() = runTest {
        // Arrange
        coEvery { mailProvider.receiveAllConversations() } returns flowOf(NetworkResponse.Loading())

        // Act
        val result = receiveAllConversationsUseCase().first()

        // Assert
        assert(result is NetworkResponse.Loading<List<MessageModelDto>>)
    }
}