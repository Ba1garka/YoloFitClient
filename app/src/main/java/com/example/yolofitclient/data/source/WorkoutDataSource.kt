package com.example.yolofitclient.data.source

import com.example.yolofitclient.data.dto.CreateWorkoutDto
import com.example.yolofitclient.data.dto.ExerciseSetDto
import com.example.yolofitclient.data.dto.TimeSlotDto
import com.example.yolofitclient.data.dto.WorkoutDetailDto
import com.example.yolofitclient.data.dto.WorkoutDto
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import okhttp3.Dispatcher

class WorkoutDataSource {
    //TODO(растащить по датасурсам)
    suspend fun createWorkout(
        userId: Int?,
        workoutDate: String,
        exerciseIds: List<Int>,
        startTime: String
    ): Result<Unit> = withContext(Dispatchers.IO) {

        val requestBody = CreateWorkoutDto(
            userId = userId,
            workoutDate = workoutDate,
            completed = false,
            exerciseIds = exerciseIds,
            startTime = startTime
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
            val result = Network.client.get("${Network.HOST}/api/workout/user/$userId") {
                addAuthHeader()
            }
            if (result.status != HttpStatusCode.OK){
                error("Status: ${result.status}")
            }
            result.body()
        }
    }

    suspend fun getWorkoutById(workoutId : Int?): Result<WorkoutDto> = withContext(Dispatchers.IO){
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/workout/$workoutId"){
                addAuthHeader()
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }

    suspend fun addExerciseSet(
        workoutId: Long,
        exerciseId: Long,
        dto: ExerciseSetDto
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val response = Network.client.post(
                "${Network.HOST}/api/exercise_sets/workout/$workoutId/exercise/$exerciseId") {
                addAuthHeader()
                contentType(ContentType.Application.Json)
                setBody(dto)
            }

            if (response.status != HttpStatusCode.Created) {
                val errorBody = response.bodyAsText()
                throw Exception("Ошибка добавления подхода: ${response.status}, $errorBody")
            }

            Unit
        }
    }

    suspend fun completeWorkout(workoutId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.put("${Network.HOST}/api/workout/complete/$workoutId") {
                addAuthHeader()
            }

            if (result.status != HttpStatusCode.OK) {
                val errorBody = result.bodyAsText()
                throw Exception("Ошибка завершения тренировки: ${result.status}")
            }

            Unit
        }
    }

    suspend fun getDailyCalories(userId: Int?): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/exercise_sets/calories/daily/$userId") {
                addAuthHeader()
            }

            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }

            result.body<Int>()
        }
    }

    suspend fun getWorkoutDetail(workoutId: Int): Result<WorkoutDetailDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/workout/$workoutId/detail") {
                addAuthHeader()
            }

            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }

            result.body<WorkoutDetailDto>()

        }
    }

    suspend fun deleteWorkout(id: Int): Result<Unit> = withContext(Dispatchers.IO){
        runCatching {
            val result = Network.client.delete("${Network.HOST}/api/workout/delete/$id"){
                addAuthHeader()
            }

            if (result.status != HttpStatusCode.OK && result.status != HttpStatusCode.NoContent) {
                error("Status: ${result.status}")
            }

            result.body<Unit>()
        }
    }

    suspend fun getSlots(userId: Int, date: String): Result<List<TimeSlotDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val response = Network.client.get("${Network.HOST}/api/workout/timeslots") {
                addAuthHeader()
                parameter("userId", userId)
                parameter("date", date)
            }
            response.body<List<TimeSlotDto>>()
        }
    }
}