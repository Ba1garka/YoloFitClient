package com.example.yolofitclient.ui.screen.home


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
import java.text.SimpleDateFormat
import java.util.*



class HomeViewModel : ViewModel() {

    private val getUserWorkoutUseCase = GetUserWorkoutUseCase( workoutRepository = WorkoutRepository(WorkoutDataSource()))


    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        loadTodayWorkouts()
    }

    fun loadTodayWorkouts() {
        viewModelScope.launch {
            _uiState.value = HomeState.Loading

            val user = AuthLocalDataSource.getCurrentUser()

            if (user.id == 0) {
                _uiState.value = HomeState.Error("Пользователь не авторизован")
                return@launch
            }

            getUserWorkoutUseCase.invoke(user.id).fold(
                onSuccess = { workouts ->
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    val todayWorkouts = workouts.filter { it.workoutDate == today }
                    _uiState.value = HomeState.Content(todayWorkouts)
                },
                onFailure = { error ->
                    _uiState.value = HomeState.Error(
                        error.message ?: "Не удалось загрузить тренировки"
                    )
                }
            )
        }
    }
}