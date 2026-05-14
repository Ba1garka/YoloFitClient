package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.ExerciseRepository
import com.example.yolofitclient.domain.entity.ExerciseEntity

class GetExerciseUseCase(
    private val exerciseRepository: ExerciseRepository
) {
    suspend operator fun invoke(): Result<List<ExerciseEntity>>{
        return exerciseRepository.getExercises()
    }
}