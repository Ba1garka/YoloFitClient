package com.example.yolofitclient.presentation.ui.screen.workout

import com.example.yolofitclient.data.dto.ExerciseSetDto
import com.example.yolofitclient.domain.entity.ExerciseEntity

sealed interface WorkoutState {

    data object Loading : WorkoutState

    data class Error(
        val reason: String
    ) : WorkoutState

    data class Content(
        val exercises: List<ExerciseEntity> = emptyList(),
        val currentExerciseIndex: Int = 0,
        val currentReps: Int = 0,
        val currentWeight: String = "",
        val isAiMode: Boolean = false,
        val completedSets: List<ExerciseSetDto> = emptyList(),
        val completedExercises: Set<Int> = emptySet(),
        val isSubmitting: Boolean = false,
        val voiceHint: String? = null
    ) : WorkoutState {
        val currentExercise: ExerciseEntity?
            get() = exercises.getOrNull(currentExerciseIndex)

        val allExercisesDone: Boolean
            get() = exercises.isNotEmpty() && completedExercises.size >= exercises.size

        val isCurrentExerciseDone: Boolean
            get() {
                val exercise = currentExercise ?: return false
                return completedExercises.contains(exercise.id)
            }

        val currentExerciseSets: Int
            get() {
                val exercise = currentExercise ?: return 0
                return completedSets.count { it.exerciseId.toInt() == exercise.id }
            }

        val hasEnoughSets: Boolean
            get() {
                val exercise = currentExercise ?: return false
                return currentExerciseSets >= exercise.defaultSets
            }
    }

    data object Success : WorkoutState
}