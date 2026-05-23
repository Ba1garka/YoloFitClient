package com.example.yolofitclient.domain.usecase


import com.example.yolofitclient.data.dto.ExerciseSetDto
import com.example.yolofitclient.data.repository.WorkoutRepository

class SubmitExerciseSetsUseCase(private val workoutRepository: WorkoutRepository) {
    suspend operator fun invoke(sets: List<ExerciseSetDto>): Result<Unit> {
        sets.forEach { set ->
            val result = workoutRepository.addExerciseSet(
                workoutId = set.workoutId,
                exerciseId = set.exerciseId,
                dto = set
            )
            if (result.isFailure) return Result.failure(result.exceptionOrNull()!!)
        }
        return Result.success(Unit)
    }
}

