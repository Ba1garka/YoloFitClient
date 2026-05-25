package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.WorkoutRepository

class GetDailyCaloriesUseCase(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(userId: Int?): Result<Int> {
        return workoutRepository.getDailyCalories(userId)
    }
}