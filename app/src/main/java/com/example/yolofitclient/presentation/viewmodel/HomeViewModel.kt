package com.example.yolofitclient.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.usecase.GetDailyCaloriesUseCase
import com.example.yolofitclient.domain.usecase.GetUserWorkoutUseCase
import com.example.yolofitclient.presentation.ui.screen.home.HomeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*



class HomeViewModel : ViewModel() {

    private val getUserWorkoutUseCase = GetUserWorkoutUseCase( workoutRepository = WorkoutRepository(WorkoutDataSource()))

    private val getDailyCaloriesUseCase = GetDailyCaloriesUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )
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

                    val caloriesResult = getDailyCaloriesUseCase.invoke(user.id)
                    println("caloriesResult: '${caloriesResult}'")
                    val dailyCalories = caloriesResult.getOrNull() ?: 0
                    println("dailyCalories: '${dailyCalories}'")

                    println("До: '${user.dailyCalorieTarget}'")

                    //TODO(сделать адекватно)

                    val dailyCalorieTarget = user.dailyCalorieTarget
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }
                        ?.toDoubleOrNull()
                        ?.toInt()

                    println("После: $dailyCalorieTarget")

                    _uiState.value = HomeState.Content(
                        todayWorkouts = todayWorkouts,
                        dailyCalories = dailyCalories,
                        dailyCalorieTarget = dailyCalorieTarget
                    )
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