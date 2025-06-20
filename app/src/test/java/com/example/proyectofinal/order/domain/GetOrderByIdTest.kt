package com.example.proyectofinal.order.domain

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.provider.OrderProvider
import com.example.proyectofinal.order.domain.usecase.GetOrderByIdUseCase
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
class GetOrderByIdUseCaseTest {

    private lateinit var repository: OrderProvider
    private lateinit var getOrderByIdUseCase: GetOrderByIdUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mockk()
        getOrderByIdUseCase = GetOrderByIdUseCase(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenRepoReturnsOrderByID_useCaseEmitsSuccess() = runTest {
        val orderId = 10
        val fakeOrder = Order(
            id = 10,
            name = "Example Order",
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

        // simulacion de la respuesta del repo al obtener una order por id
        coEvery { repository.getOrderById(orderId) } returns flow {
            emit(NetworkResponse.Success(fakeOrder))
        }

        val results = mutableListOf<NetworkResponse<Order>>()

        // llamamos al cu y recolectamos los resultados
        getOrderByIdUseCase(orderId).toList(results)

        // Verifacamos que el resultado sea Success y contenga la order correcta
        assert(results.first() is NetworkResponse.Success)
        assertEquals(fakeOrder, (results.first() as NetworkResponse.Success).data)
    }
}
