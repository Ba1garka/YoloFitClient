package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.UserRepository
import com.example.yolofitclient.domain.entity.UserEntity

class RegisterUseCase( private val userRepository: UserRepository) {
    suspend operator fun invoke(
        name: String,
        email: String,
        birthDate: String,
        gender: String,
        height: String,
        weight: String,
        fitnessLevel: String,
        password: String,
        goal: String
    ): Result<UserEntity>{
        return userRepository.register(
            name,
            email,
            birthDate,
            gender,
            height,
            weight,
            fitnessLevel,
            password,
            goal
        )
    }
}