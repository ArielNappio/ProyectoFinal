package com.example.proyectofinal

import android.app.Application
import com.example.proyectofinal.di.networkModule
import com.example.proyectofinal.di.repositoryModule
import com.example.proyectofinal.di.tokenManagerModule
import com.example.proyectofinal.di.useCaseModule
import com.example.proyectofinal.di.viewModelModule
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