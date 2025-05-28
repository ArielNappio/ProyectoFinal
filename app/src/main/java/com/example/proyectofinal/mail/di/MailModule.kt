package com.example.proyectofinal.mail.di

import androidx.room.Room
import com.example.proyectofinal.mail.data.local.MailDatabase
import com.example.proyectofinal.mail.data.provider.MailProviderImpl
import com.example.proyectofinal.mail.data.repository.MailRepoImpl
import com.example.proyectofinal.mail.domain.provider.MailProvider
import com.example.proyectofinal.mail.domain.repository.MailRepository
import com.example.proyectofinal.mail.domain.usecase.DeleteMessageUseCase
import com.example.proyectofinal.mail.domain.usecase.GetDraftMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetInboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.GetOutboxMessagesUseCase
import com.example.proyectofinal.mail.domain.usecase.ReceiveMessageUseCase
import com.example.proyectofinal.mail.domain.usecase.SaveDraftUseCase
import com.example.proyectofinal.mail.domain.usecase.SendMessageUseCase
import com.example.proyectofinal.mail.presentation.viewmodel.InboxViewModel
import com.example.proyectofinal.mail.presentation.viewmodel.MessageViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mailDatabaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            MailDatabase::class.java,
            "mail_database"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

}

val mailModule = module {
    single<MailProvider> { MailProviderImpl(get()) }
    single<MailRepository> { MailRepoImpl(get(), get()) }
    single { get<MailDatabase>().messageDao() }
    factory { SendMessageUseCase(get()) }
    factory { ReceiveMessageUseCase(get()) }
    factory { GetInboxMessagesUseCase(get()) }
    factory { GetOutboxMessagesUseCase(get()) }
    factory { SaveDraftUseCase(get()) }
    factory { DeleteMessageUseCase(get()) }
    factory { GetDraftMessagesUseCase(get()) }
    viewModel { InboxViewModel(get(), get(), get()) }
    viewModel { MessageViewModel(get(), get()) }
}
