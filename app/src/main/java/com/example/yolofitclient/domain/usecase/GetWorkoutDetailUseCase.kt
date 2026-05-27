package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.domain.entity.WorkoutDetailEntity


class GetWorkoutDetailUseCase(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke(workoutId: Int): Result<WorkoutDetailEntity> {
        return workoutRepository.getWorkoutDetail(workoutId)
    }
}