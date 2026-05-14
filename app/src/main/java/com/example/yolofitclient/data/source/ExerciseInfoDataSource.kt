package com.example.yolofitclient.data.source

import com.example.yolofitclient.data.dto.ExerciseDto
import io.ktor.client.call.body
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode

class ExerciseInfoDataSource {

    suspend fun getExercises(): Result<List<ExerciseDto>> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/exercise"){
                addAuthHeader()
            }
            if (result.status != HttpStatusCode.OK){
                error("Status: ${result.status}")
            }
            result.body()
        }

    }
}