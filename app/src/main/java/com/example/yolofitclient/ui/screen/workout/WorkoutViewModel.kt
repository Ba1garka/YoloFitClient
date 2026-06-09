package com.example.yolofitclient.ui.screen.workout

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.dto.ExerciseSetDto
import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.entity.TrackingConfigEntity
import com.example.yolofitclient.domain.usecase.CompleteWorkoutUseCase
import com.example.yolofitclient.domain.usecase.GetWorkoutByIdUseCase
import com.example.yolofitclient.domain.usecase.SubmitExerciseSetsUseCase
import com.example.yolofitclient.nn.ExerciseCounter
import com.example.yolofitclient.nn.FeedbackManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class WorkoutViewModel(
    private val workoutId: Int
) : ViewModel() {

    private val _restSeconds = MutableStateFlow(0)
    val restSeconds: StateFlow<Int> = _restSeconds.asStateFlow()

    private val _isResting = MutableStateFlow(false)
    val isResting: StateFlow<Boolean> = _isResting.asStateFlow()

    fun getRestSeconds(): Int = _restSeconds.value
    fun isResting(): Boolean = _isResting.value

    private fun startRestTimer() {
        _isResting.value = true
        _restSeconds.value = 20
        viewModelScope.launch {
            while (_restSeconds.value > 0) {
                delay(1000L)
                _restSeconds.value -= 1
            }
            _isResting.value = false
        }
    }

    private val getWorkoutByIdUseCase = GetWorkoutByIdUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )

    private val submitSetsUseCase = SubmitExerciseSetsUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )

    private val completeWorkoutUseCase = CompleteWorkoutUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )

    private val _uiState: MutableStateFlow<WorkoutState> = MutableStateFlow(WorkoutState.Loading)
    val uiState: StateFlow<WorkoutState> = _uiState.asStateFlow()

    init {
        loadWorkout()
    }

    private fun loadWorkout() {
        viewModelScope.launch {
            _uiState.value = WorkoutState.Loading

            getWorkoutByIdUseCase.invoke(workoutId).fold(
                onSuccess = { workout ->

                    _uiState.value = WorkoutState.Content(
                        exercises = workout.exercises
                    )
                },
                onFailure = { error ->
                    _uiState.value = WorkoutState.Error(
                        reason = error.message ?: "Не удалось загрузить тренировку"
                    )
                }
            )
        }
    }

    fun selectExercise(index: Int) {
        val state = _uiState.value
        if (state is WorkoutState.Content) {
            _uiState.value = state.copy(
                currentExerciseIndex = index,
                currentReps = 0,
                currentWeight = ""
            )
        }
    }

    fun incrementReps() {
        val state = _uiState.value
        if (state is WorkoutState.Content) {
            _uiState.value = state.copy(currentReps = state.currentReps + 1)
        }
    }

    fun decrementReps() {
        val state = _uiState.value
        if (state is WorkoutState.Content && state.currentReps > 0) {
            _uiState.value = state.copy(currentReps = state.currentReps - 1)
        }
    }

    fun updateWeight(weight: String) {
        val state = _uiState.value
        if (state is WorkoutState.Content) {
            _uiState.value = state.copy(currentWeight = weight)
        }
    }

    fun completeSet() {
        val state = _uiState.value
        if (state is WorkoutState.Content && state.currentReps > 0) {
            val currentExercise = state.currentExercise ?: return

            val newSet = ExerciseSetDto(
                id = null,
                workoutId = workoutId.toLong(),
                exerciseId = currentExercise.id.toLong(),
                exerciseName = currentExercise.name,
                setNumber = state.completedSets.filter {
                    it.workoutId == workoutId.toLong() && it.exerciseId == currentExercise.id.toLong()
                }.size + 1,
                repsDone = state.currentReps,
                weightDone = state.currentWeight.toDoubleOrNull(),
                mistakeCount = 0,
                caloriesBurned = 0.0
            )

            _uiState.value = state.copy(
                completedSets = state.completedSets + newSet,
                currentReps = 0,
                currentWeight = ""
            )
        }
    }

    fun markExerciseCompleted() {
        val state = _uiState.value
        if (state is WorkoutState.Content) {
            val currentExercise = state.currentExercise ?: return
            val updatedCompleted = state.completedExercises + currentExercise.id

            val nextIndex = if (state.currentExerciseIndex < state.exercises.size - 1) {
                state.currentExerciseIndex + 1
            } else {
                state.currentExerciseIndex
            }

            _uiState.value = state.copy(
                completedExercises = updatedCompleted,
                currentExerciseIndex = nextIndex,
                currentReps = 0,
                currentWeight = ""
            )
        }
    }

    fun toggleAiMode() {
        val state = _uiState.value
        if (state is WorkoutState.Content) {
            val currentExercise = state.currentExercise
            if (currentExercise?.trackingConfig != null || !state.isAiMode) {
                _uiState.value = state.copy(
                    isAiMode = !state.isAiMode,
                    currentReps = 0
                )
            }
        }
    }

    fun updateAiReps(reps: Int) {
        val state = _uiState.value
        if (state is WorkoutState.Content) {
            _uiState.value = state.copy(currentReps = reps)
            val exercise = state.currentExercise ?: return
            if (reps >= exercise.defaultReps && reps > 0 && !_isResting.value) {
                completeSet()
                startRestTimer()
            }
        }
    }

    fun finishWorkout() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state !is WorkoutState.Content) return@launch

            _uiState.value = state.copy(isSubmitting = true)

            val result = submitSetsUseCase(state.completedSets)
            if (result.isFailure) {
                _uiState.value = WorkoutState.Error(
                    reason = result.exceptionOrNull()?.message ?: "Ошибка сохранения подходов"
                )
                return@launch
            }

            completeWorkoutUseCase(workoutId).fold(
                onSuccess = { _uiState.value = WorkoutState.Success },
                onFailure = { error ->
                    _uiState.value = WorkoutState.Error(
                        reason = error.message ?: "Ошибка завершения тренировки"
                    )
                }
            )
        }
    }

    fun retry() {
        loadWorkout()
    }




    private var feedbackManager: FeedbackManager? = null
    fun initAiSession(context: Context, config: TrackingConfigEntity) {
        feedbackManager?.shutdown()
        feedbackManager = FeedbackManager(context, config)
        feedbackManager?.init()
    }

    fun processAiFeedback(
        angle: Double,
        phase: ExerciseCounter.Phase,
    ) {
        val state = _uiState.value
        if (state !is WorkoutState.Content) return

        val hint = feedbackManager?.analyzeAndGiveFeedback(
            currentAngle = angle,
            detectedPhase = phase
        )
        if (hint != null) {
            _uiState.value = ( _uiState.value as? WorkoutState.Content)?.copy(voiceHint = hint)!!
        }
    }

    fun clearHint() {
        val state = _uiState.value
        if (state is WorkoutState.Content) {
            _uiState.value = state.copy(voiceHint = null)
        }
    }

    override fun onCleared() {
        feedbackManager?.shutdown()
        super.onCleared()
    }
}