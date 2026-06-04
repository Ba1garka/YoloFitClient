package com.example.yolofitclient.domain.usecase


import com.example.yolofitclient.data.repository.WorkoutRepository

class CreateWorkoutUseCase(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(
        userId: Int?,
        workoutDate: String,
        exerciseIds: List<Int>,
        startTime: String
    ): Result<Unit> {
        return workoutRepository.createWorkout(
            userId = userId,
            workoutDate = workoutDate,
            exerciseIds = exerciseIds,
            startTime = startTime
        )
    }
}