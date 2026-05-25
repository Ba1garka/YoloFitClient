package com.example.yolofitclient.data.repository

import com.example.yolofitclient.data.source.AuthNetworkDataSource
import com.example.yolofitclient.domain.entity.UserEntity

class UserRepository(
    private val authNetworkDataSource: AuthNetworkDataSource
) {

    suspend fun register(
        name: String,
        email: String,
        birthDate: String,
        gender: String,
        height: String,
        weight: String,
        fitnessLevel: String,
        password: String
    ): Result<UserEntity>{
        return authNetworkDataSource.register(
            name,
            email,
            birthDate,
            gender,
            height,
            weight,
            fitnessLevel,
            password,
        ).mapCatching { userDto ->
            UserEntity(
                id = userDto.id ?: throw Exception("ID is null"),
                name = userDto.name ?: throw Exception("Name is null"),
                email = userDto.email ?: throw Exception("Email is null"),
                birthDate = userDto.birthDate ?: throw Exception("BirthDate is null"),
                gender = userDto.gender ?: throw Exception("Gender is null"),
                height = userDto.height ?: throw Exception("Height is null"),
                weight = userDto.weight ?: throw Exception("Weight is null"),
                fitnessLevel = userDto.fitnessLevel ?: throw Exception("FitnessLevel is null"),
                goal = userDto.goal ?: throw Exception("Goal is null"),
                dailyCalorieTarget = userDto.dailyCalorieTarget ?: throw Exception("DailyCalorieTarget is null")
            )
        }
    }
}