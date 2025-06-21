package com.example.proyectofinal.core.di

import com.example.proyectofinal.auth.data.tokenmanager.TokenManager
import com.example.proyectofinal.auth.presentation.viewmodel.LoginViewModel
import com.example.proyectofinal.camera.presentation.viewmodel.CameraViewModel
import com.example.proyectofinal.core.ui.ThemeViewModel
import com.example.proyectofinal.navigation.presentation.viewmodel.MainViewModel
import com.example.proyectofinal.student.presentation.viewmodel.CommentsViewModel
import com.example.proyectofinal.student.presentation.viewmodel.DetailsViewModel
import com.example.proyectofinal.student.presentation.viewmodel.HomeScreenViewModel
import com.example.proyectofinal.student.presentation.viewmodel.ProjectDetailViewModel
import com.example.proyectofinal.task_student.presentation.viewmodel.TaskStudentViewModel
import com.example.proyectofinal.text_editor.presentation.viewmodel.TextEditorViewModel
import com.example.proyectofinal.users.presentation.viewmodel.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::CameraViewModel)
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get()) }
    viewModel { HomeScreenViewModel(get(), get(), get()) }
    viewModel { ProjectDetailViewModel(get(), get<TokenManager>(), get()) }
    viewModel { DetailsViewModel(get()) }
    viewModel { TaskStudentViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { CommentsViewModel(get(),get())}
    single { ThemeViewModel(get()) }
    viewModel { TextEditorViewModel(get(), get(), get()) }
    viewModel { UserViewModel(get(), get() , get() , get()) }
}
