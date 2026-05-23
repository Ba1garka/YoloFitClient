package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.domain.entity.WorkoutEntity

class GetWorkoutByIdUseCase(private val workoutRepository: WorkoutRepository) {
    suspend operator fun invoke(id: Int): Result<WorkoutEntity>{
        return workoutRepository.getWorkoutById(id)
    }
}