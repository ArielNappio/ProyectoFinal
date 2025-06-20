package com.example.proyectofinal.order.domain

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.provider.OrderProvider
import com.example.proyectofinal.order.domain.usecase.GetOrdersUseCase
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetOrdersUseCaseTest {

    private lateinit var repository: OrderProvider
    private lateinit var getOrdersUseCase: GetOrdersUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mockk()
        getOrdersUseCase = GetOrdersUseCase(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenRepoReturnsOrders_useCaseEmitsSuccess() = runTest {
        val fakeOrders = listOf(
            Order(
                id = 1,
                name = "Example Order 1",
                subject = "Mathematics",
                description = "This is an example order for a math assignment.",
                authorName = "John Doe",
                rangePage = "1-10",
                isPriority = true,
                state = "Pending",
                dateCreation = "2023-10-01",
                limitDate = "2023-10-15",
                createdByUserId = "123",
                filePath = "/path/to/file.pdf",
                voluntarioId = "456",
                alumnoId = "789",
                revisorId = "101",
                delivererId = "112"
            ),
            Order(
                id = 2,
                name = "Example Order 2",
                subject = "Mathematics",
                description = "This is an example order for a math assignment.",
                authorName = "John Doe",
                rangePage = "1-10",
                isPriority = true,
                state = "Pending",
                dateCreation = "2023-10-01",
                limitDate = "2023-10-15",
                createdByUserId = "123",
                filePath = "/path/to/file.pdf",
                voluntarioId = "456",
                alumnoId = "789",
                revisorId = "101",
                delivererId = "112"
            ),
            Order(
                id = 3,
                name = "Example Order 3",
                subject = "Mathematics",
                description = "This is an example order for a math assignment.",
                authorName = "John Doe",
                rangePage = "1-10",
                isPriority = true,
                state = "Pending",
                dateCreation = "2023-10-01",
                limitDate = "2023-10-15",
                createdByUserId = "123",
                filePath = "/path/to/file.pdf",
                voluntarioId = "456",
                alumnoId = "789",
                revisorId = "101",
                delivererId = "112"
            )
        )

        coEvery { repository.getOrders() } returns flow {
            emit(NetworkResponse.Success(fakeOrders))
        }

        val results = mutableListOf<NetworkResponse<List<Order>>>()

        getOrdersUseCase().toList(results)

        assert(results.first() is NetworkResponse.Success)
        assertEquals(fakeOrders, (results.first() as NetworkResponse.Success).data)
    }
}
