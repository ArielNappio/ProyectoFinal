package com.example.proyectofinal.order.domain

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.provider.OrderProvider
import com.example.proyectofinal.order.domain.usecase.CreateOrderUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
class CreateOrderUseCaseTest {

    private lateinit var repository: OrderProvider
    private lateinit var createOrderUseCase: CreateOrderUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mockk()
        createOrderUseCase = CreateOrderUseCase(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenRepoCreatesOrder_useCaseEmitsSuccessAndCallsRepoCreateOrder(): Unit = runTest {
        val orderToCreate = Order(
            id = 1,
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

        // Simulación de la respuesta cuando se crea una order
        coEvery { repository.createOrder(orderToCreate) } returns flow {
            emit(NetworkResponse.Success(orderToCreate))
        }

        val results = mutableListOf<NetworkResponse<Order>>()

        // Llamada al cu
        createOrderUseCase(orderToCreate).toList(results)

        // Verificar que el repo recibió la order para crearla
        coVerify { repository.createOrder(orderToCreate) }

        // Valida que el resultado sea el esperado (Success)
        assert(results.first() is NetworkResponse.Success)
        assertEquals(orderToCreate, (results.first() as NetworkResponse.Success).data)
    }
}