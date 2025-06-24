package com.example.proyectofinal.orderManagement.di

import androidx.room.Room
import com.example.proyectofinal.orderManagement.data.local.LastPageReadDatabase
import com.example.proyectofinal.orderManagement.data.local.OrderDatabase
import com.example.proyectofinal.orderManagement.data.provider.OrderManagementImpl
import com.example.proyectofinal.orderManagement.data.repository.LastReadRepository
import com.example.proyectofinal.orderManagement.data.repository.OrderRepositoryImpl
import com.example.proyectofinal.orderManagement.domain.provider.OrderManagementProvider
import com.example.proyectofinal.orderManagement.domain.repository.OrderRepository
import com.example.proyectofinal.orderManagement.domain.usecase.GetTaskGroupByStudentUseCase
import com.example.proyectofinal.orderManagement.domain.usecase.UpdateFavoriteStatusUseCase
import org.koin.dsl.module

val orderDatabaseModule = module {
    single {
        Room.databaseBuilder(get(), OrderDatabase::class.java, "orders-db")
//            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, Migration_3_4)
            .fallbackToDestructiveMigration()
            .build()
    }
    single { get<OrderDatabase>().orderDao() }
}

val lastReadModule = module {
    single {
        Room.databaseBuilder(
            get(),
            LastPageReadDatabase::class.java,
            "app_database"
        ).build()
    }

    single { get<LastPageReadDatabase>().lastPageReadDao() }

    single { LastReadRepository(get()) }
}


val orderModule = module {
    single<OrderManagementProvider> { OrderManagementImpl(get(), get()) }
    factory { GetTaskGroupByStudentUseCase(get()) }
    factory { UpdateFavoriteStatusUseCase(get()) }
    single <OrderRepository> { OrderRepositoryImpl(get(), get()) }
}
