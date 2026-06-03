package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.WorkoutRepository

class DeleteWorkoutUseCase( private val workoutRepository: WorkoutRepository) {
    suspend operator fun invoke(listId: List<Int>): Result<Unit> {
        listId.forEach { id ->
            val result = workoutRepository.deleteWorkout(id)
            if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        }
        return Result.success(Unit)
    }
}