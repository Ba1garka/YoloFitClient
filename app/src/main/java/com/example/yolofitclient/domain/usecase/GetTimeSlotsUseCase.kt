package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.domain.entity.TimeSlotEntity

class GetTimeSlotsUseCase(
    private val workoutRepository: WorkoutRepository
) {
    suspend operator fun invoke( userId: Int, date: String): Result<List<TimeSlotEntity>>{
        return workoutRepository.getSlots(userId, date)
    }
}