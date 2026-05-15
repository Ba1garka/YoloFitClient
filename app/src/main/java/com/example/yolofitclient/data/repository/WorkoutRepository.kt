package com.example.yolofitclient.data.repository

import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.entity.ExerciseEntity
import com.example.yolofitclient.domain.entity.WorkoutEntity

class WorkoutRepository( private val workoutDataSource: WorkoutDataSource) {

    suspend fun createWorkout(
        userId: Int?,
        workoutDate: String,
        exerciseIds: List<Int>
    ): Result<Unit> {
        return workoutDataSource.createWorkout(
            userId = userId,
            workoutDate = workoutDate,
            exerciseIds = exerciseIds
        )
    }

    suspend fun getUserWorkouts(userId: Int?): Result<List<WorkoutEntity>>{
        return workoutDataSource.getUserWorkouts(userId).map { listDto ->
            listDto.mapNotNull { dto ->
                WorkoutEntity(
                    id = dto.id ?: return@mapNotNull null,
                    userId = dto.userId ?: return@mapNotNull null,
                    userName = dto.userName ?: return@mapNotNull null,
                    workoutDate = dto.workoutDate ?: return@mapNotNull null,
                    completed = dto.completed ?: return@mapNotNull null,
                    exercises = dto.exercises?.map { dto ->
                        ExerciseEntity(
                            id = dto.id ?: return@mapNotNull null,
                            name = dto.name ?: return@mapNotNull null,
                            defaultSets = dto.defaultSets ?: return@mapNotNull null,
                            defaultReps = dto.defaultReps ?: return@mapNotNull null,
                            weightCoefficient = dto.weightCoefficient ?: return@mapNotNull null,
                            bodyZoneName = dto.bodyZoneName ?: return@mapNotNull null,
                        )
                    } ?: emptyList()
                )
            }
        }
    }

}