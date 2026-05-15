package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.domain.entity.WorkoutEntity

class GetUserWorkoutUseCase( private val workoutRepository: WorkoutRepository) {
    suspend operator fun invoke(id: Int?): Result<List<WorkoutEntity>>{
        return workoutRepository.getUserWorkouts(id)
    }
}