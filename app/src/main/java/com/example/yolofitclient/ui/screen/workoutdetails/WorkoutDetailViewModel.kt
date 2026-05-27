package com.example.yolofitclient.ui.screen.workoutdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.WorkoutRepository
import com.example.yolofitclient.data.source.WorkoutDataSource
import com.example.yolofitclient.domain.usecase.GetWorkoutDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class WorkoutDetailViewModel(
    private val workoutId: Int
) : ViewModel() {

    private val getWorkoutDetailUseCase = GetWorkoutDetailUseCase(
        workoutRepository = WorkoutRepository(WorkoutDataSource())
    )

    private val _uiState = MutableStateFlow<WorkoutDetailState>(WorkoutDetailState.Loading)
    val uiState: StateFlow<WorkoutDetailState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = WorkoutDetailState.Loading

            getWorkoutDetailUseCase(workoutId).fold(
                onSuccess = { detail ->
                    _uiState.value = WorkoutDetailState.Content(detail)
                },
                onFailure = { error ->
                    _uiState.value = WorkoutDetailState.Error(
                        reason = error.message ?: "Не удалось загрузить детали"
                    )
                }
            )
        }
    }
}

