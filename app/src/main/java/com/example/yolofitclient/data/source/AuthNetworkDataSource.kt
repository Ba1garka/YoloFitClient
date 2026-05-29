package com.example.yolofitclient.data.source

import com.example.yolofitclient.data.dto.CreateWorkoutDto
import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.dto.UserRegisterDto
import com.example.yolofitclient.domain.entity.UserEntity
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class AuthNetworkDataSource {

    suspend fun checkAuth(): Result<UserDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/users/login") {
                addAuthHeader()
            }
            if (result.status == HttpStatusCode.OK){
                result.body<UserDto>()
            } else {
                throw Exception("Ошибка получения профиля: ${result.status}")
            }
        }
    }

    suspend fun register(
        name: String,
        email: String,
        birthDate: String,
        gender: String,
        height: String,
        weight: String,
        fitnessLevel: String,
        password: String
    ): Result<UserDto> = withContext(Dispatchers.IO) { //TODO in own datasourse

        val requestBody = UserRegisterDto(
            name = name,
            email = email,
            birthDate = birthDate,
            gender = gender,
            height = height,
            weight = weight,
            fitnessLevel = fitnessLevel,
            password = password,
            goal = "BUILD_MUSCLE"
        )
        runCatching {
            val result = Network.client.post("${Network.HOST}/api/users/register"){
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            if (result.status == HttpStatusCode.Conflict){
                val errorBody = result.bodyAsText()
                println("Пользователь уже существует. Ответ: $errorBody")
                throw Exception("Пользователь с email $email уже существует")
            }
            result.body<UserDto>()
        }
    }

    suspend fun updateUser(
        user: UserDto,
    ): Result<UserDto> = withContext(Dispatchers.IO) {

        runCatching {
            val result = Network.client.put("${Network.HOST}/api/users/update/${user.id}") {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(user)
            }

            if (result.status == HttpStatusCode.Conflict) {
                val errorBody = result.bodyAsText()
                println("409 Conflict: $errorBody")
                throw Exception(errorBody)
            }

            if (result.status != HttpStatusCode.OK) {
                val errorBody = result.bodyAsText()
                throw Exception("Ошибка обновления профиля: ${result.status}")
            }

            result.body<UserDto>()
        }

    }
}