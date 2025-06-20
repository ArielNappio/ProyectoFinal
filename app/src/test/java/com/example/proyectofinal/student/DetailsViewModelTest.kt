package com.example.proyectofinal.student

import com.example.proyectofinal.student.data.repository.TaskRepository
import com.example.proyectofinal.student.domain.model.Task
import com.example.proyectofinal.student.presentation.viewmodel.DetailsViewModel
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    private lateinit var viewModel: DetailsViewModel
    private lateinit var repository: TaskRepository
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setup() {
        repository = mockk()
        viewModel = DetailsViewModel(repository)
    }

    @Test
    fun `getTaskById should update task state`() = testScope.runTest {
        // Arrange
        val taskId = 1
        val taskStub = Task(
            id = taskId,
            name = "Test Task",
            description = "This is a test task",
            lastRead = "2023-10-01",
            pageCount = 10,
            isFavorite = true,
            hasComments = true
        )
        //TODO
//        coEvery { repository.getTaskById(taskId) } returns taskStub

        // Act
        viewModel.getTaskById(taskId)

        // Assert
//        assertEquals(taskStub, viewModel.task.first())
    }
}