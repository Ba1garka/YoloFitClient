package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.WorkoutRepository

class CompleteWorkoutUseCase(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(
        workoutId: Int
    ): Result<Unit> {
        return workoutRepository.completeWorkout(workoutId)
    }
}