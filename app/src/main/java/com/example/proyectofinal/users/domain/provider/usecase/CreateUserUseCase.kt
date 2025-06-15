package com.example.proyectofinal.users.domain.provider.usecase

import com.example.proyectofinal.users.data.model.User
import com.example.proyectofinal.users.data.provider.UserProviderImpl

class CreateUserUseCase (private val createUserRepistory : UserProviderImpl){
    operator fun invoke(user: User) = createUserRepistory.createUser(user)
}


