package com.example.proyectofinal.librarian.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.order.data.model.Order
import com.example.proyectofinal.order.domain.usecase.CreateOrderUseCase
import com.example.proyectofinal.order.domain.usecase.GetOrdersUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

    class CreateOrderViewModel(
        private val getOrdersUseCase: GetOrdersUseCase,
        private val createOrderUseCase: CreateOrderUseCase

    ) : ViewModel() {

        private val _id = MutableStateFlow(0)
        val id: StateFlow<Int> = _id.asStateFlow()

        private val _name = MutableStateFlow("")
        val name: StateFlow<String> = _name.asStateFlow()

        private val _description = MutableStateFlow("")
        val description: StateFlow<String> = _description.asStateFlow()

        private val _status = MutableStateFlow("")
        val status: StateFlow<String> = _status.asStateFlow()

        private val _creationDate = MutableStateFlow("")
        val creationDate: StateFlow<String> = _creationDate.asStateFlow()

        private val _limitDate = MutableStateFlow(Date())
        val limitDate: StateFlow<Date> = _limitDate.asStateFlow()

        private val _createdByUserId = MutableStateFlow("")
        val createdByUserId: StateFlow<String> = _createdByUserId.asStateFlow()

        private val _filePath = MutableStateFlow("")
        val filePath: StateFlow<String> = _filePath.asStateFlow()

        private val _assignedUserId = MutableStateFlow("")
        val assignedUserId: StateFlow<String> = _assignedUserId.asStateFlow()

        private val _isFavorite = MutableStateFlow(false)
        val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

        private val _lastRead = MutableStateFlow("")
        val lastRead: StateFlow<String> = _lastRead.asStateFlow()

        private val _pageCount = MutableStateFlow(0)
        val pageCount: StateFlow<Int> = _pageCount.asStateFlow()

        private val _hasComments = MutableStateFlow(false)
        val hasComments: StateFlow<Boolean> = _hasComments.asStateFlow()

        private val _file = MutableStateFlow(File(""))
        val file: StateFlow<File> = _file.asStateFlow()



        fun onIdChanged(newId: Int) {
            _id.value = newId
        }

        fun onNameChanged(newName: String) {
            _name.value = newName
        }

        fun onDescriptionChanged(newDescription: String) {
            _description.value = newDescription
        }

        fun onStatusChanged(newStatus: String) {
            _status.value = newStatus
        }

        fun onCreationDateChanged(newDate: String) {
            _creationDate.value = newDate
        }

        fun onLimitDateChanged(newDate: Date) {
            _limitDate.value = newDate
        }

        fun onCreatedByUserIdChanged(newId: String) {
            _createdByUserId.value = newId
        }

        fun onFilePathChanged(newPath: String) {
            _filePath.value = newPath
        }

        fun onAssignedUserIdChanged(newId: String) {
            _assignedUserId.value = newId
        }

        fun onIsFavoriteChanged(isFav: Boolean) {
            _isFavorite.value = isFav
        }

        fun onLastReadChanged(newLastRead: String) {
            _lastRead.value = newLastRead
        }

        fun onPageCountChanged(newCount: Int) {
            _pageCount.value = newCount
        }

        fun onHasCommentsChanged(has: Boolean) {
            _hasComments.value = has
        }

        fun onFileChanged(newFile: File) {
            _file.value = newFile
        }





        fun createOrder(order: Order) {
            viewModelScope.launch {
                createOrderUseCase(order)
            }
        }


    }