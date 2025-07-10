package com.example.proyectofinal.order.domain

import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.provider.OrderProvider
import com.example.proyectofinal.order.domain.usecase.UpdateOrderUseCase
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
class UpdateOrderUseCaseTest {

    private lateinit var repository: OrderProvider
    private lateinit var updateOrderUseCase: UpdateOrderUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mockk()
        updateOrderUseCase = UpdateOrderUseCase(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenRepoUpdatesOrder_useCaseEmitsSuccess() = runTest {
        val orderToUpdate = Order(
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

        // Simulamos respuesta exitosa
        coEvery { repository.updateOrder(orderToUpdate) } returns flow {
            emit(NetworkResponse.Success(Unit))
        }

        val results = mutableListOf<NetworkResponse<Unit>>()

        // Ejecutamos el caso de uso
        updateOrderUseCase(orderToUpdate).toList(results)

        // Comprobamos que la primera emisión sea Success y contenga Unit
        assert(results.first() is NetworkResponse.Success)
        assertEquals(Unit, (results.first() as NetworkResponse.Success).data)
    }
}
