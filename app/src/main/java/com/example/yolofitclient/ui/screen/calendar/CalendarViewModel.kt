package com.example.yolofitclient.ui.screen.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.usecase.GetUserWorkoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {

    private val getUserWorkoutUseCase = GetUserWorkoutUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )

    private val _uiState = MutableStateFlow<CalendarState>(CalendarState.Loading)
    val uiState: StateFlow<CalendarState> = _uiState.asStateFlow()

    init {
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            _uiState.value = CalendarState.Loading

            val user = AuthLocalDataSource.getCurrentUser()
            if (user.id == 0) {
                _uiState.value = CalendarState.Error("Пользователь не авторизован")
                return@launch
            }

            getUserWorkoutUseCase(user.id).fold(
                onSuccess = { workouts ->
                    _uiState.value = CalendarState.Content(workouts)
                },
                onFailure = { error ->
                    _uiState.value = CalendarState.Error(
                        error.message ?: "Не удалось загрузить тренировки"
                    )
                }
            )
        }
    }
}