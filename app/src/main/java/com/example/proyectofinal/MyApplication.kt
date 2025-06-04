package com.example.proyectofinal

import android.app.Application
import com.example.proyectofinal.auth.di.repositoryModule
import com.example.proyectofinal.auth.di.tokenManagerModule
import com.example.proyectofinal.core.di.networkModule
import com.example.proyectofinal.core.di.useCaseModule
import com.example.proyectofinal.core.di.viewModelModule
import com.example.proyectofinal.student.data.di.CommentsRepositoryModule
import com.example.proyectofinal.student.data.di.taskRepositoryModule
import com.example.proyectofinal.task_student.di.ttsModule
import com.example.proyectofinal.users.data.di.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(
                networkModule,
                repositoryModule,
                viewModelModule,
                useCaseModule,
                tokenManagerModule,
                taskRepositoryModule,
                ttsModule,
                CommentsRepositoryModule,
                repositoryModule,
                userModule
                )
        }

    }

}