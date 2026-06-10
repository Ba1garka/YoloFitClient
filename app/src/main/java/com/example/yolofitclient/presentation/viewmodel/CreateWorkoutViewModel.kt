package com.example.yolofitclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.usecase.CreateWorkoutUseCase
import com.example.yolofitclient.domain.usecase.GetTimeSlotsUseCase
import com.example.yolofitclient.presentation.ui.screen.createworkout.CreateWorkoutState
import com.example.yolofitclient.presentation.ui.screen.createworkout.SlotsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateWorkoutViewModel: ViewModel() {

    private val createWorkoutUseCase = CreateWorkoutUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )

    private val getTimeSlotsUseCase = GetTimeSlotsUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )
    private val _uiState : MutableStateFlow<CreateWorkoutState> = MutableStateFlow(
        CreateWorkoutState.Content)
    val uiState = _uiState.asStateFlow()

    private val _timeSlotsState: MutableStateFlow<SlotsState> = MutableStateFlow(SlotsState.Loading)
    val timeSlotsState = _timeSlotsState.asStateFlow()

    fun createWorkout(
        workoutDate: String,
        exerciseIds: List<Int>,
        startTime: String
    ){
        viewModelScope.launch {
            _uiState.emit(CreateWorkoutState.Loading)

            val user = AuthLocalDataSource.getCurrentUser()

            createWorkoutUseCase.invoke(
                userId = user.id,
                workoutDate = workoutDate,
                exerciseIds = exerciseIds,
                startTime = startTime
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

    fun getTime(data: String){
        viewModelScope.launch {
            _timeSlotsState.emit(SlotsState.Loading)

            val user = AuthLocalDataSource.getCurrentUser()

            getTimeSlotsUseCase.invoke(user.id!!, data).fold(
                onSuccess = { data ->
                    _timeSlotsState.emit(SlotsState.Content(data))
                },
                onFailure = { error ->
                    _timeSlotsState.emit(SlotsState.Error(error.message.orEmpty()))
                }
            )
        }
    }
}