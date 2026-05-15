package com.example.yolofitclient.data.source

import com.example.yolofitclient.data.dto.CreateWorkoutDto
import com.example.yolofitclient.data.dto.WorkoutDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode

class WorkoutDataSource {

    suspend fun createWorkout(
        userId: Int?,
        workoutDate: String,
        exerciseIds: List<Int>
    ): Result<Unit> = withContext(Dispatchers.IO) {

        val requestBody = CreateWorkoutDto(
            userId = userId,
            workoutDate = workoutDate,
            completed = false,
            exerciseIds = exerciseIds
        )

        runCatching {
            val result = Network.client.post("${Network.HOST}/api/workout/create") {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }

            if (result.status == HttpStatusCode.Conflict) {
                val errorBody = result.bodyAsText()
                throw Exception("Тренировка на дату $workoutDate уже существует")
            }

            if (result.status != HttpStatusCode.OK && result.status != HttpStatusCode.Created) {
                val errorBody = result.bodyAsText()
                throw Exception("Ошибка создания тренировки: ${result.status}")
            }
            Unit
        }

    }

    suspend fun getUserWorkouts(userId: Int?): Result<List<WorkoutDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/workout/$userId") {
                addAuthHeader()
            }
            if (result.status != HttpStatusCode.OK){
                error("Status: ${result.status}")
            }
            result.body()
        }
    }

}