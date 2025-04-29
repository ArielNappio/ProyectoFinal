package com.example.proyectofinal

import com.example.proyectofinal.data.remoteData.model.Order
import com.example.proyectofinal.data.remoteData.repository.RemoteRepository
import com.example.proyectofinal.domain.usecase.UpdateOrderUseCase
import com.example.proyectofinal.util.NetworkResponse
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

    private lateinit var repository: RemoteRepository
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
            id = 5,
            name = "Orden actualizada",
            description = "Descripcion actualizada",
            state = "En proceso",
            dateCreation = "",
            dateLimit = "",
            userId = 4
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
