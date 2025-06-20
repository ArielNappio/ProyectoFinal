package com.example.proyectofinal.users

import UpdateUserUseCase
import android.util.Log
import com.example.proyectofinal.core.network.NetworkResponse
import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.data.provider.UserProviderImpl
import com.example.proyectofinal.users.domain.provider.usecase.CreateUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.DeleteUserUseCase
import com.example.proyectofinal.users.domain.provider.usecase.GetUserUseCase
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
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
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserViewModelTest {

    private lateinit var userViewModel: UserViewModel
    private lateinit var userProviderMock: UserProviderImpl

    private val testDispatcher = StandardTestDispatcher()

    val mockUser1 = User("1", "user1", "User One", "user1@example.com", "password", "1234567890", listOf("role1"))
    val mockUser2 = User("2", "user2", "User Two", "user2@example.com", "password", "0987654321", listOf("role2"))

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0

        userProviderMock = mockk()
        coEvery { userProviderMock.getUsers() } returns flow {
            emit(NetworkResponse.Success(listOf(mockUser1, mockUser2)))
        }
        coEvery { userProviderMock.deleteUser(any()) } returns flow {
            emit(NetworkResponse.Success(Unit))
        }
        coEvery { userProviderMock.updateUser(any(), any()) } returns flow {
            emit(NetworkResponse.Success(Unit))
        }
        coEvery { userProviderMock.createUser(any()) } returns flow {
            emit(NetworkResponse.Success(mockUser2))
        }

        userViewModel = UserViewModel(
            GetUserUseCase(userProviderMock),
            DeleteUserUseCase(userProviderMock),
            UpdateUserUseCase(userProviderMock),
            CreateUserUseCase(userProviderMock)
        )
    }

    @Test
    fun `fetchUsers should update users state on success`() = runTest {
        userViewModel.fetchUsers()
        advanceUntilIdle()

        assertEquals(listOf(mockUser1, mockUser2), userViewModel.users.value)
    }

    @Test
    fun `deleteUser should call deleteUserUseCase and fetchUsers`() = runTest {
        coEvery { userProviderMock.getUsers() } returns flow {
            emit(NetworkResponse.Success(listOf(mockUser2)))
        }
        userViewModel.deleteUser("1")
        advanceUntilIdle()

        assertEquals(listOf(mockUser2), userViewModel.users.value)
    }

    @Test
    fun `changedUser should call updateUserUseCase and fetchUsers`() = runTest {
        val userId = "1"
        val updatedUser = User("1", "updatedUser", "Updated User", "updated@example.com", "password", "1234567890", listOf("role1"))
        coEvery { userProviderMock.updateUser(userId, updatedUser) } returns flow {
            emit(NetworkResponse.Success(Unit))
        }
        coEvery { userProviderMock.getUsers() } returns flow {
            emit(NetworkResponse.Success(listOf(updatedUser, mockUser2)))
        }

        userViewModel.changedUser(userId, updatedUser)
        advanceUntilIdle()

        assertEquals(listOf(updatedUser, mockUser2), userViewModel.users.value)
    }

    @Test
    fun `createdUser should call createUserUseCase`() = runTest {
        val newUser = User("3", "newUser", "New User", "new@example.com", "password", "1234567890", listOf("role1"))
        coEvery { userProviderMock.getUsers() } returns flow {
            emit(NetworkResponse.Success(listOf(mockUser1, mockUser2, newUser)))
        }
        userViewModel.createdUser(newUser)
        advanceUntilIdle()

        coVerify { userProviderMock.createUser(any()) }
    }
}