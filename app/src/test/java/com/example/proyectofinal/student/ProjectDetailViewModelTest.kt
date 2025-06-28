package com.example.proyectofinal.student

import app.cash.turbine.test
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.data.repository.LastReadRepository
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.model.OrderStudent
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.orderManagement.domain.usecase.UpdateFavoriteStatusUseCase
import com.example.proyectofinal.student.presentation.viewmodel.ProjectDetailViewModel
import com.example.proyectofinal.userpreferences.domain.repository.UserPreferencesRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProjectDetailViewModelTest {

    private lateinit var viewModel: ProjectDetailViewModel
    private lateinit var getOrders: GetTaskGroupByStudentUseCase
    private lateinit var tokenManager: TokenManager
    private lateinit var updateFavoriteStatusUseCase: UpdateFavoriteStatusUseCase
    private lateinit var lastReadRepository: LastReadRepository

    private val repository: UserPreferencesRepository = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    val userId = "123"
    val projectId = "order-001"
    val orderStudentMock = OrderStudent(
        id = 1,
        name = "Example Task",
        description = "This is a description of the task.",
        isFavorite = true,
        lastRead = "2023-10-01",
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
        getOrders = mockk()
        tokenManager = mockk()
        updateFavoriteStatusUseCase = mockk()
        coEvery { tokenManager.userId } returns flowOf(userId)
        coEvery { getOrders(userId) } returns flowOf(
            NetworkResponse.Success(orderDeliveredMock)
        )
        lastReadRepository = mockk()
        coEvery { lastReadRepository.getLastReadPage(any()) } returns 1

        viewModel = ProjectDetailViewModel(
            getOrders,
            tokenManager,
            updateFavoriteStatusUseCase,
            repository,
            lastReadRepository
        )
    }

    @Test
    fun `loadProject should update projectState with success response`() = testScope.runTest {
        viewModel.loadProject(projectId)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.projectState.test {
            skipItems(1)
            val state = awaitItem()
            assert(state is NetworkResponse.Success)
            assertEquals(projectId, (state as NetworkResponse.Success).data!!.id)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadProject should update projectState with failure when project not found`() =
        testScope.runTest {
            coEvery { getOrders(userId) } returns flowOf(NetworkResponse.Success(emptyList()))

            viewModel.loadProject(projectId)
            testDispatcher.scheduler.advanceUntilIdle()

            viewModel.projectState.test {
                skipItems(1)
                val state = awaitItem()
                assert(state is NetworkResponse.Failure)
                assertEquals(
                    "Proyecto no encontrado",
                    (state as NetworkResponse.Failure).error
                )
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun `toggleFavorite should update projectState with updated favorite status`() =
        testScope.runTest {
            val isFavorite = true

            coEvery { updateFavoriteStatusUseCase(projectId, isFavorite) } just Runs

            viewModel.loadProject(projectId)
            viewModel.projectState.test {
                skipItems(2)
                cancelAndConsumeRemainingEvents()
                viewModel.toggleFavorite(projectId, isFavorite)
                testDispatcher.scheduler.advanceUntilIdle()
                viewModel.projectState.test {
                    val state = awaitItem()
                    assert(state is NetworkResponse.Success)
                    assertEquals(isFavorite, (state as NetworkResponse.Success).data!!.isFavorite)
                    cancelAndConsumeRemainingEvents()
                }
            }
        }
}