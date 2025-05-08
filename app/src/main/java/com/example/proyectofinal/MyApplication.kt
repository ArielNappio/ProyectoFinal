package com.example.proyectofinal

import android.app.Application
import com.example.proyectofinal.core.di.networkModule
import com.example.proyectofinal.auth.di.repositoryModule
import com.example.proyectofinal.auth.di.tokenManagerModule
import com.example.proyectofinal.core.di.useCaseModule
import com.example.proyectofinal.core.di.viewModelModule
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
                tokenManagerModule
            )
        }

    }

}