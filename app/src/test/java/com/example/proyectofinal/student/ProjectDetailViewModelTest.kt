package com.example.proyectofinal.student

import app.cash.turbine.test
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.data.repository.LastReadRepository
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.model.OrderStudent
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.orderManagement.domain.usecase.UpdateFavoriteStatusUseCase
import com.example.proyectofinal.student.presentation.viewmodel.ProjectDetailViewModel
import com.example.proyectofinal.userpreferences.domain.model.UserPreferences
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.clearAllMocks
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProjectDetailViewModelTest {

    private lateinit var viewModel: ProjectDetailViewModel
    private lateinit var tokenManager: TokenManager
    private lateinit var updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
    private lateinit var lastReadRepository: LastReadRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var userPreferencesRepository: UserPreferencesRepository

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    val userId = "123"
    val projectId = "order-001"
    val orderStudentMock = OrderStudent(
        id = 1,
        name = "Example Task",
        description = "This is a description of the task.",
        isFavorite = false,
        lastRead = "1",
        pageCount = 100,
        hasComments = false,
        subject = "Math",
        authorName = "John Doe",
        rangePage = "1-10",
        status = "Completed",
        voluntarioId = "vol-123",
        alumnoId = "alum-456"
    )
    val orderDeliveredMock = listOf(
        OrderDelivered(
            studentId = "12345",
            id = projectId,
            status = "Delivered",
            title = "Order Title Example",
            orders = listOf(orderStudentMock),
            orderParagraphs = listOf(
                OrderParagraph(
                    orderId = 1,
                    paragraphText = "This is an example paragraph.",
                    pageNumber = 1
                )
            )
        )
    )

    @Before
    fun setUp() {
        tokenManager = mockk()
        updateFavoriteStatusUseCase = mockk()
        lastReadRepository = mockk()
        orderRepository = mockk()
        userPreferencesRepository = mockk()

        coEvery { userPreferencesRepository.getUserPreferences() } returns flowOf(
            UserPreferences(
                iconSize = 24f,
                fontSize = 24f,
                fontFamily = "Default"
            )
        )

        coEvery { tokenManager.userId } returns flowOf(userId)

        coEvery { lastReadRepository.getLastReadPage(any()) } returns 1

        viewModel = ProjectDetailViewModel(
            GetTaskGroupByStudentUseCase(orderRepository),
            tokenManager,
            updateFavoriteStatusUseCase,
            userPreferencesRepository,
            lastReadRepository
        )

        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        clearAllMocks()
        testScope.cancel()
    }

    @Test
    fun `loadProject should update projectState with success response`() = testScope.runTest {
        coEvery { orderRepository.getTasks(userId) } returns flowOf(
            NetworkResponse.Success(orderDeliveredMock)
        )

        viewModel.projectState.test {
            assert(awaitItem() is NetworkResponse.Loading)

            viewModel.loadProject(projectId)
            testDispatcher.scheduler.advanceUntilIdle()

            val successState = awaitItem()
            assertTrue(successState is NetworkResponse.Success)
            assertEquals(projectId, (successState as NetworkResponse.Success).data?.id)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadProject should update projectState with failure response when unauthenticated`() =
        testScope.runTest {
            coEvery { tokenManager.userId } returns flowOf(null)

            viewModel.projectState.test {
                assert(awaitItem() is NetworkResponse.Loading)

                viewModel.loadProject(projectId)
                testDispatcher.scheduler.advanceUntilIdle()

                val failureState = awaitItem()
                assertTrue(failureState is NetworkResponse.Failure)
                assertEquals(
                    "Usuario no autenticado",
                    (failureState as NetworkResponse.Failure).error
                )

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `loadProject should update projectState with failure response when unexpected error`() =
        testScope.runTest {
            coEvery { orderRepository.getTasks(userId) } returns flow {
                throw RuntimeException("Unexpected error")
            }

            viewModel.projectState.test {
                assert(awaitItem() is NetworkResponse.Loading)

                viewModel.loadProject(projectId)
                testDispatcher.scheduler.advanceUntilIdle()

                val failureState = awaitItem()
                assertTrue(failureState is NetworkResponse.Failure)
                assertEquals("Unexpected error", (failureState as NetworkResponse.Failure).error)

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `loadProject should update projectState with failure when project not found`() =
        testScope.runTest {
            coEvery { orderRepository.getTasks(userId) } returns flowOf(
                NetworkResponse.Success(emptyList())
            )

            viewModel.projectState.test {
                assert(awaitItem() is NetworkResponse.Loading)

                viewModel.loadProject(projectId)
                testDispatcher.scheduler.advanceUntilIdle()

                val failureState = awaitItem()
                assertTrue(failureState is NetworkResponse.Failure)
                assertEquals(
                    "Proyecto no encontrado",
                    (failureState as NetworkResponse.Failure).error
                )

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `loadProject should update projectState with failure when network error`() =
        testScope.runTest {
            coEvery { orderRepository.getTasks(userId) } returns flowOf(
                NetworkResponse.Failure("network error")
            )

            viewModel.projectState.test {
                assert(awaitItem() is NetworkResponse.Loading)

                viewModel.loadProject(projectId)
                testDispatcher.scheduler.advanceUntilIdle()

                val failureState = awaitItem()
                assertTrue(failureState is NetworkResponse.Failure)
                assertEquals(
                    "network error",
                    (failureState as NetworkResponse.Failure).error
                )

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `toggleFavorite should update projectState with updated favorite status`() =
        testScope.runTest {
            val updatedOrderDeliveredMock = orderDeliveredMock[0].copy(isFavorite = true)

            coEvery { orderRepository.getTasks(userId) } returns flowOf(
                NetworkResponse.Success(orderDeliveredMock)
            )

            coEvery { updateFavoriteStatusUseCase(projectId, true) } just Runs

            viewModel.projectState.test {
                assert(awaitItem() is NetworkResponse.Loading)

                viewModel.loadProject(projectId)
                testDispatcher.scheduler.advanceUntilIdle()

                val initialSuccessState = awaitItem()
                assertTrue(initialSuccessState is NetworkResponse.Success)
                assertEquals(projectId, (initialSuccessState as NetworkResponse.Success).data?.id)
                assertEquals(orderStudentMock.isFavorite, initialSuccessState.data?.isFavorite)

                viewModel.toggleFavorite(projectId, true)
                testDispatcher.scheduler.advanceUntilIdle()

                val toggledSuccessState = awaitItem()
                assertTrue(toggledSuccessState is NetworkResponse.Success)
                assertEquals(
                    true,
                    (toggledSuccessState as NetworkResponse.Success).data?.isFavorite
                )
                assertEquals(
                    updatedOrderDeliveredMock.copy(isFavorite = true),
                    toggledSuccessState.data
                )

                cancelAndConsumeRemainingEvents()
            }
        }
}