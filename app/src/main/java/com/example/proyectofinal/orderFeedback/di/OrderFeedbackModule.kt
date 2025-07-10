package com.example.proyectofinal.orderFeedback.di

import androidx.room.Room
import com.example.proyectofinal.orderFeedback.data.local.FeedbackDao
import com.example.proyectofinal.orderFeedback.data.local.FeedbackDatabase
import com.example.proyectofinal.orderFeedback.data.provider.OrderFeedbackImpl
import com.example.proyectofinal.orderFeedback.data.repository.FeedbackRepositoryImpl
import com.example.proyectofinal.orderFeedback.domain.local.FeedbackRepository
import com.example.proyectofinal.orderFeedback.domain.provider.OrderFeedbackProvider
import com.example.proyectofinal.orderFeedback.domain.usecase.GetFeedbackUseCase
import com.example.proyectofinal.orderFeedback.domain.usecase.SaveFeedbackUseCase
import com.example.proyectofinal.orderFeedback.domain.usecase.SendFeedbackUseCase
import org.koin.dsl.module

val feedbackModule = module{
    single {
        Room.databaseBuilder(
            context = get(),
            FeedbackDatabase::class.java,
            "feedback_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    single<FeedbackDao> { get<FeedbackDatabase>().feedbackDao() }
    single<FeedbackRepository> { FeedbackRepositoryImpl(get()) }
    single { FeedbackRepositoryImpl(get()) }
    factory { GetFeedbackUseCase(get()) }
    factory { SaveFeedbackUseCase(get())}

    single<OrderFeedbackProvider> { OrderFeedbackImpl(get()) }
    factory { SendFeedbackUseCase(get()) }
}