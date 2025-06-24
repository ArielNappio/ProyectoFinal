package com.example.proyectofinal.orderFeedback.di

import com.example.proyectofinal.orderFeedback.data.provider.OrderFeedbackImpl
import com.example.proyectofinal.orderFeedback.domain.provider.OrderFeedbackProvider
import com.example.proyectofinal.orderFeedback.domain.usecase.SendFeedbackUseCase
import org.koin.dsl.module

val feedbackModule = module{
    single<OrderFeedbackProvider> { OrderFeedbackImpl(get()) }
    factory { SendFeedbackUseCase(get()) }
}