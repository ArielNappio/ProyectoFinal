package com.example.proyectofinal.student

import com.example.proyectofinal.student.data.repository.TaskRepository
import com.example.proyectofinal.student.domain.model.Task
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeScreenViewModelTest {

    private lateinit var repository: TaskRepository
    private lateinit var viewModel: HomeScreenViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.getAllTasks() } returns MutableStateFlow(emptyList<Task>())
        viewModel = HomeScreenViewModel(repository)
    }

    @Test
    fun `tasks flow should emit data from repository`() = testScope.runTest {
        val mockTasks = listOf(
            Task(1, "Task 1", "Task description", false, "2023-10-01", 10, false),
            Task(2, "Task 2", "Another task description", true, "2023-10-02", 20, true)
        )
        every { repository.getAllTasks() } returns MutableStateFlow(mockTasks)

        viewModel = HomeScreenViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(mockTasks, viewModel.tasks.value)
    }

    @Test
    fun `updateSearchText should update searchText state`() = testScope.runTest {
        val newText = "New search text"
        viewModel.updateSearchText(newText)

        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(newText, viewModel.searchText.value)
    }

    @Test
    fun `toggleFavorite should call repository toggleFavorite`() = testScope.runTest {
        val taskId = 1
        every { repository.toggleFavorite(taskId) } just Runs

        viewModel.toggleFavorite(taskId)

        coVerify { repository.toggleFavorite(taskId) }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }
}