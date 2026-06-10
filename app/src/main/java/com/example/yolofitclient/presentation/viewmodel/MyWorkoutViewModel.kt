package com.example.yolofitclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.usecase.DeleteWorkoutUseCase
import com.example.yolofitclient.domain.usecase.GetUserWorkoutUseCase
import com.example.yolofitclient.presentation.ui.screen.myworkout.MyWorkoutState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyWorkoutViewModel: ViewModel() {

    private val deleteWorkoutUseCase = DeleteWorkoutUseCase(WorkoutRepository(WorkoutDataSource()))

    private val getUserWorkoutUseCase = GetUserWorkoutUseCase(WorkoutRepository(WorkoutDataSource()))

    private val _uiState : MutableStateFlow<MyWorkoutState> = MutableStateFlow(MyWorkoutState.Loading)
    val uiState = _uiState.asStateFlow()


    init {
        getWorkouts()
    }

    fun delete(listId: List<Int>) {
        viewModelScope.launch {
            _uiState.emit(MyWorkoutState.Loading)

            deleteWorkoutUseCase.invoke(listId).fold(
                onSuccess = {
                    _uiState.emit(MyWorkoutState.Success)
                    delay(1000) //TODO(сделать адекватно)
                    getWorkouts()
                },
                onFailure = { error ->
                    _uiState.emit(MyWorkoutState.Error(error.message.toString()))
                }
            )
        }
    }

    fun getWorkouts() {
        viewModelScope.launch {
            _uiState.emit(MyWorkoutState.Loading)

            val user = AuthLocalDataSource.getCurrentUser()

            if (user.id == 0) {
                _uiState.emit(MyWorkoutState.Error("Пользователь не авторизован!"))
                return@launch
            }

            getUserWorkoutUseCase.invoke(user.id).fold(
                onSuccess = { workouts ->
                    _uiState.emit(MyWorkoutState.Content(workouts))
                },
                onFailure = { error ->
                    _uiState.emit(MyWorkoutState.Error(error.message.toString()))
                }
            )
        }
    }
}