package com.example.proyectofinal.order.presentation

import android.util.Log
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.order.domain.model.Order
import com.example.proyectofinal.order.domain.usecase.CreateOrderUseCase
import com.example.proyectofinal.order.domain.usecase.DeleteOrderUseCase
import com.example.proyectofinal.order.domain.usecase.GetOrderByIdUseCase
import com.example.proyectofinal.order.domain.usecase.GetOrdersUseCase
import com.example.proyectofinal.order.domain.usecase.UpdateOrderUseCase
import com.example.proyectofinal.order.presentation.viewmodel.OrderViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class OrderViewModelTest {

    private lateinit var viewModel: OrderViewModel
    private val getOrdersUseCase: GetOrdersUseCase = mockk()
    private val getOrderByIdUseCase: GetOrderByIdUseCase = mockk()
    private val createOrderUseCase: CreateOrderUseCase = mockk()
    private val updateOrderUseCase: UpdateOrderUseCase = mockk()
    private val deleteOrderUseCase: DeleteOrderUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private val orderStub =
        Order(1, "Order 1", "Order 1 description", "state", "2025-07-01", "2025-07-08", 1)

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        Dispatchers.setMain(testDispatcher)
        viewModel = OrderViewModel(
            getOrdersUseCase,
            getOrderByIdUseCase,
            createOrderUseCase,
            updateOrderUseCase,
            deleteOrderUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getOrderById should update selectedOrder state with success response`() = runTest {
        coEvery { getOrderByIdUseCase(1) } returns flow {
            emit(NetworkResponse.Success(orderStub))
        }

        viewModel.getOrderById(1)

        advanceUntilIdle()

        assertEquals(orderStub, viewModel.selectedOrder.value)
        coVerify { getOrderByIdUseCase(1) }
    }

    @Test
    fun `createOrder should call createOrderUseCase and fetchOrders`() = runTest {
        coEvery { createOrderUseCase(orderStub) } returns flow {
            emit(NetworkResponse.Success(orderStub))
        }
        coEvery { getOrdersUseCase() } returns flow {
            emit(NetworkResponse.Success(emptyList()))
        }

        viewModel.createOrder(orderStub)

        advanceUntilIdle()

        coVerify { createOrderUseCase(orderStub) }
        coVerify { getOrdersUseCase() }
    }

    @Test
    fun `updateOrder should call updateOrderUseCase and fetchOrders`() = runTest {
        coEvery { updateOrderUseCase(orderStub) } returns flow {
            emit(NetworkResponse.Success(Unit))
        }
        coEvery { getOrdersUseCase() } returns flow {
            emit(NetworkResponse.Success(emptyList()))
        }

        viewModel.updateOrder(orderStub)

        advanceUntilIdle()

        coVerify { updateOrderUseCase(orderStub) }
        coVerify { getOrdersUseCase() }
    }

    @Test
    fun `deleteOrder should call deleteOrderUseCase and fetchOrders`() = runTest {
        coEvery { deleteOrderUseCase(1) } returns flow {
            emit(NetworkResponse.Success(Unit))
        }
        coEvery { getOrdersUseCase() } returns flow {
            emit(NetworkResponse.Success(emptyList()))
        }

        viewModel.deleteOrder(1)

        advanceUntilIdle()

        coVerify { deleteOrderUseCase(1) }
        coVerify { getOrdersUseCase() }
    }
}