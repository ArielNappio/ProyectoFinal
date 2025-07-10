package com.example.proyectofinal

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.proyectofinal.audio.di.audioModule
import com.example.proyectofinal.audio.di.databaseModule
import com.example.proyectofinal.auth.di.repositoryModule
import com.example.proyectofinal.auth.di.tokenManagerModule
import com.example.proyectofinal.core.di.networkModule
import com.example.proyectofinal.core.di.useCaseModule
import com.example.proyectofinal.core.di.viewModelModule
import com.example.proyectofinal.mail.di.mailDatabaseModule
import com.example.proyectofinal.mail.di.mailModule
import com.example.proyectofinal.orderFeedback.di.feedbackModule
import com.example.proyectofinal.orderManagement.di.lastReadModule
import com.example.proyectofinal.orderManagement.di.orderDatabaseModule
import com.example.proyectofinal.orderManagement.di.orderModule
import com.example.proyectofinal.task_student.di.downloadFileUseCasesModule
import com.example.proyectofinal.task_student.di.ttsModule
import com.example.proyectofinal.text_editor.di.pdfBitmapConverterModule
import com.example.proyectofinal.text_editor.di.pdfRemoteRepositoryModule
import com.example.proyectofinal.userpreferences.di.preferencesModule
import com.example.proyectofinal.users.data.di.userModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class WirinApplication: Application() {

    @RequiresApi(Build.VERSION_CODES.O)
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
                ttsModule,
                downloadFileUseCasesModule,
                mailDatabaseModule,
                mailModule,
                preferencesModule,
                pdfBitmapConverterModule,
                pdfRemoteRepositoryModule,
                orderModule,
                orderDatabaseModule,
                userModule,
                feedbackModule,
                userModule,
                lastReadModule
            )
        }

    }

}