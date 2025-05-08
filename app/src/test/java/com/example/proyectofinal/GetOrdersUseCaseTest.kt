package com.example.proyectofinal

import com.example.proyectofinal.order.data.model.Order
import com.example.proyectofinal.auth.data.remoteData.repository.AuthRemoteRepository
import com.example.proyectofinal.order.domain.usecase.GetOrdersUseCase
import com.example.proyectofinal.core.network.NetworkResponse
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

    private lateinit var repository: AuthRemoteRepository
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
                1, "Tarea 1",
                description = "description",
                state = "En process",
                dateCreation = "",
                dateLimit = "",
                userId = 3
            ), Order(
                1, "Tarea 2",
                description = "description",
                state = "Por hacer",
                dateCreation = "",
                dateLimit = "",
                userId = 2
            ),
            Order(
                1, "Tarea 3",
                description = "description",
                state = "Terminado",
                dateCreation = "",
                dateLimit = "",
                userId = 5
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
