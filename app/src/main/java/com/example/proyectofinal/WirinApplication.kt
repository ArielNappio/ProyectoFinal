package com.example.proyectofinal

import android.app.Application
import com.example.proyectofinal.audio.di.audioModule
import com.example.proyectofinal.audio.di.databaseModule
import com.example.proyectofinal.auth.di.repositoryModule
import com.example.proyectofinal.auth.di.tokenManagerModule
import com.example.proyectofinal.core.di.networkModule
import com.example.proyectofinal.core.di.useCaseModule
import com.example.proyectofinal.core.di.viewModelModule
import com.example.proyectofinal.mail.di.mailDatabaseModule
import com.example.proyectofinal.mail.di.mailModule
import com.example.proyectofinal.orderManagment.di.orderModule
import com.example.proyectofinal.student.data.di.CommentsRepositoryModule
import com.example.proyectofinal.student.data.di.taskProviderModule
import com.example.proyectofinal.student.data.di.taskRepositoryModule
import com.example.proyectofinal.task_student.di.ttsModule
import com.example.proyectofinal.userpreferences.di.preferencesModule
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
                audioModule,
                databaseModule,
                networkModule,
                repositoryModule,
                viewModelModule,
                useCaseModule,
                tokenManagerModule,
                taskRepositoryModule,
                taskProviderModule,
                ttsModule,
                CommentsRepositoryModule,
                mailDatabaseModule,
                mailModule,
                preferencesModule,
                pdfBitmapConverterModule,
                pdfRemoteRepositoryModule,
                orderModule
            )
        }

    }

}