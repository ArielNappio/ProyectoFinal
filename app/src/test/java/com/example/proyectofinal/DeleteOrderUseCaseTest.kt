package com.example.proyectofinal

import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.order.domain.usecase.DeleteOrderUseCase
import com.example.proyectofinal.core.network.NetworkResponse
import io.mockk.coEvery
import io.mockk.mockk
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
class DeleteOrderUseCaseTest {

    private lateinit var repository: AuthRemoteRepository
    private lateinit var deleteOrderUseCase: DeleteOrderUseCase

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        repository = mockk()
        deleteOrderUseCase = DeleteOrderUseCase(repository)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenRepositoryDeletesOrder_useCaseEmitsSuccess() = runTest {
        val orderIdToDelete = 5

        // simulacion de la respuesta del repo al eliminar una order
        coEvery { repository.deleteOrder(orderIdToDelete) } returns flow {
            emit(NetworkResponse.Success(Unit))
        }

        val results = mutableListOf<NetworkResponse<Unit>>()

        //Llamada al cu
        deleteOrderUseCase(orderIdToDelete).toList(results)

        // Verificar que la respuesta fue Success
        assert(results.first() is NetworkResponse.Success)
    }
}