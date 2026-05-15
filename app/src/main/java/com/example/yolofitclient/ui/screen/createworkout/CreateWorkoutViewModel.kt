package com.example.yolofitclient.ui.screen.createworkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.usecase.CreateWorkoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateWorkoutViewModel: ViewModel() {

    private val createWorkoutUseCase = CreateWorkoutUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )
    private val _uiState : MutableStateFlow<CreateWorkoutState> = MutableStateFlow(CreateWorkoutState.Content)
    val uiState = _uiState.asStateFlow()

    fun createWorkout(
        workoutDate: String,
        exerciseIds: List<Int>,
    ){
        viewModelScope.launch {
            _uiState.emit(CreateWorkoutState.Loading)

            val user = AuthLocalDataSource.getCurrentUser()

            createWorkoutUseCase.invoke(
                userId = user.id,
                workoutDate = workoutDate,
                exerciseIds = exerciseIds
            ).fold(
                onSuccess = { _ ->
                    _uiState.emit(CreateWorkoutState.Success)
                },
                onFailure = { error ->
                    _uiState.emit(CreateWorkoutState.Error(error.message.toString()))
                }
            )
        }
    }
}