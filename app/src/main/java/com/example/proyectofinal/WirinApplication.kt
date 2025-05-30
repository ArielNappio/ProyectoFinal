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
import com.example.proyectofinal.text_editor.di.pdfBitmapConverterModule
import com.example.proyectofinal.text_editor.di.pdfRemoteRepositoryModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class WirinApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@WirinApplication)
            modules(
                networkModule,
                repositoryModule,
                viewModelModule,
                useCaseModule,
                tokenManagerModule,
                taskRepositoryModule,
                ttsModule,
                pdfBitmapConverterModule,
                pdfRemoteRepositoryModule,
                CommentsRepositoryModule
            )
        }

    }

}