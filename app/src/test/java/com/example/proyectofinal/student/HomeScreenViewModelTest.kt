package com.example.proyectofinal.student

import android.util.Log
import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.orderManagement.domain.model.OrderDelivered
import com.example.proyectofinal.orderManagement.domain.model.OrderParagraph
import com.example.proyectofinal.orderManagement.domain.model.OrderStudent
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.orderManagement.domain.usecase.UpdateFavoriteStatusUseCase
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private lateinit var viewModel: HomeScreenViewModel
    private lateinit var repository: OrderRepository
    private lateinit var tokenManager: TokenManager

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    val mockTasks = listOf(
        OrderDelivered(
            studentId = "12345",
            id = "order-001",
            status = "Delivered",
            title = "Order Title Example",
            orders = listOf(
                OrderStudent(
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
            ),
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
        mockkStatic(Log::class)
        // Define behavior for Log.e
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.getTasks(any()) } returns flowOf(NetworkResponse.Success(mockTasks))
        tokenManager = mockk()
        every { tokenManager.userId } returns flowOf("example_id")
        val getTaskGroupByStudentUseCase = GetTaskGroupByStudentUseCase(repository)
        val updateFavoriteStatusUseCase = UpdateFavoriteStatusUseCase(repository)

        viewModel = HomeScreenViewModel(getTaskGroupByStudentUseCase, tokenManager, updateFavoriteStatusUseCase)
    }

    @Test
    fun `tasks flow should emit data from repository`() = testScope.runTest {
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(mockTasks, viewModel.orders.value)
    }

    @Test
    fun `updateSearchText should update searchText state`() = testScope.runTest {
        val newText = "New search text"
        viewModel.updateSearchText(newText)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(newText, viewModel.searchText.value)
    }

//    @Test
//    fun `toggleFavorite should call repository toggleFavorite`() = testScope.runTest {
//        val taskId = 1
//        every { repository.toggleFavorite(taskId) } just Runs
//
//        viewModel.toggleFavorite(taskId)
//
//        coVerify { repository.toggleFavorite(taskId) }
//    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}