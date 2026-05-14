package com.example.yolofitclient.data.repository

import com.example.yolofitclient.data.source.ExerciseInfoDataSource
import com.example.yolofitclient.domain.entity.ExerciseEntity

class ExerciseRepository(
    private val exerciseInfoDataSource: ExerciseInfoDataSource
) {
    suspend fun getExercises(): Result<List<ExerciseEntity>>{
        return exerciseInfoDataSource.getExercises().map { listDto ->
            listDto.mapNotNull { dto ->
                ExerciseEntity(
                    name = dto.name ?: return@mapNotNull null,
                    defaultSets = dto.defaultSets ?: return@mapNotNull null,
                    defaultReps = dto.defaultReps ?: return@mapNotNull null,
                    weightCoefficient = dto.weightCoefficient ?: return@mapNotNull null,
                    bodyZoneName = dto.bodyZoneName ?: return@mapNotNull null,
                )
            }
        }
    }
}