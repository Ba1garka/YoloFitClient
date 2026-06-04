package com.example.yolofitclient.data.repository

import com.example.yolofitclient.data.dto.ExerciseSetDto
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.entity.ExerciseEntity
import com.example.yolofitclient.domain.entity.ExerciseSetDetailEntity
import com.example.yolofitclient.domain.entity.TimeSlotEntity
import com.example.yolofitclient.domain.entity.TrackingConfigEntity
import com.example.yolofitclient.domain.entity.WorkoutDetailEntity
import com.example.yolofitclient.domain.entity.WorkoutEntity

class WorkoutRepository( private val workoutDataSource: WorkoutDataSource) {

    suspend fun createWorkout(
        userId: Int?,
        workoutDate: String,
        exerciseIds: List<Int>,
        startTime: String
    ): Result<Unit> {
        return workoutDataSource.createWorkout(
            userId = userId,
            workoutDate = workoutDate,
            exerciseIds = exerciseIds,
            startTime = startTime
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
                    } ?: emptyList(),
                    startTime = dto.startTime ?: return@mapNotNull null
                )
            }
        }
    }

    suspend fun getWorkoutById(id: Int): Result<WorkoutEntity> {
        return workoutDataSource.getWorkoutById(id).mapCatching { dto ->
            WorkoutEntity(
                id = dto.id ?: 0,
                userId = dto.userId ?: 0,
                userName = dto.userName ?: "",
                workoutDate = dto.workoutDate ?: "",
                completed = dto.completed ?: false,
                exercises = dto.exercises?.map { exerciseDto ->
                    ExerciseEntity(
                        id = exerciseDto.id ?: 0,
                        name = exerciseDto.name ?: "",
                        defaultSets = exerciseDto.defaultSets ?: 0,
                        defaultReps = exerciseDto.defaultReps ?: 0,
                        weightCoefficient = exerciseDto.weightCoefficient ?: "0",
                        bodyZoneName = exerciseDto.bodyZoneName ?: "",
                        trackingConfig = exerciseDto.trackingConfig?.let { configDto ->
                            TrackingConfigEntity(
                                id = configDto.id ?: 0,
                                exerciseId = configDto.exerciseId ?: 0,
                                exerciseName = configDto.exerciseName ?: "",
                                jointIndices = configDto.jointIndices ?: "",
                                angleDown = configDto.angleDown ?: 0.0,
                                angleUp = configDto.angleUp ?: 0.0,
                                countDirection = configDto.countDirection ?: "",
                                minConfidence = configDto.minConfidence ?: 0.4,
                                framesToConfirm = configDto.framesToConfirm ?: 3,
                                description = configDto.description,
                                bendHint = configDto.bendHint,
                                straightenHint = configDto.straightenHint
                            )
                        }
                    )
                } ?: emptyList(),
                startTime = dto.startTime ?: ""
            )

        }
    }

    suspend fun addExerciseSet(
        workoutId: Long,
        exerciseId: Long,
        dto: ExerciseSetDto
    ): Result<Unit> {
        return workoutDataSource.addExerciseSet(workoutId,exerciseId, dto)
    }

    suspend fun completeWorkout(workoutId: Int): Result<Unit> {
        return workoutDataSource.completeWorkout(workoutId)
    }

    suspend fun getDailyCalories(userId: Int?): Result<Int> {
        return workoutDataSource.getDailyCalories(userId)
    }

    suspend fun getWorkoutDetail(id: Int) : Result<WorkoutDetailEntity> {
        return workoutDataSource.getWorkoutDetail(id).mapCatching { dto ->
            WorkoutDetailEntity(
                id = dto.id ?: 0,
                userId = dto.userId ?: 0,
                userName = dto.userName,
                workoutDate = dto.workoutDate ?: "",
                completed = dto.completed ?: false,
                totalCalories = dto.totalCalories ?: 0.0,
                exerciseSets = dto.exerciseSets?.map { setDto ->
                    ExerciseSetDetailEntity(
                        id = setDto.id ?: 0,
                        exerciseName = setDto.exerciseName ?: "",
                        setNumber = setDto.setNumber ?: 0,
                        repsDone = setDto.repsDone,
                        weightDone = setDto.weightDone,
                        caloriesBurned = setDto.caloriesBurned ?: 0.0,
                        mistakeCount = setDto.mistakeCount ?: 0,
                    )
                } ?: emptyList(),
                startTime = dto.startTime ?: ""
            )
        }
    }

    suspend fun deleteWorkout(id: Int) : Result<Unit> {
        return workoutDataSource.deleteWorkout(id)
    }

    suspend fun getSlots(userId: Int, date: String): Result<List<TimeSlotEntity>>{
        return workoutDataSource.getSlots( userId, date).mapCatching{ listDto ->
            listDto.mapNotNull { dto ->
                TimeSlotEntity(
                    date = dto.date ?: return@mapNotNull null,
                    startTime = dto.startTime ?: return@mapNotNull null,
                    available = dto.available ?: return@mapNotNull null,
                )
            }
        }
    }
}