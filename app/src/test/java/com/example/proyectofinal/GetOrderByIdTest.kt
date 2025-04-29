package com.example.proyectofinal

import com.example.proyectofinal.data.remoteData.model.Order
import com.example.proyectofinal.data.remoteData.repository.RemoteRepository
import com.example.proyectofinal.domain.usecase.GetOrderByIdUseCase
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
class GetOrderByIdUseCaseTest {

    private lateinit var repository: RemoteRepository
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
            id = orderId,
            name = "Orden de prueba",
            description = "Test order",
            state = "En proceso",
            dateCreation = "",
            dateLimit = "",
            userId = 4
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
