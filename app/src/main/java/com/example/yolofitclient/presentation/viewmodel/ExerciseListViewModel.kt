package com.example.yolofitclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.ExerciseRepository
import com.example.yolofitclient.data.source.ExerciseInfoDataSource
import com.example.yolofitclient.domain.usecase.GetExerciseUseCase
import com.example.yolofitclient.presentation.ui.screen.exercise.ExerciseListState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ExerciseListViewModel: ViewModel() {

    private val getExercisesUseCase = GetExerciseUseCase(
        exerciseRepository = ExerciseRepository(
            exerciseInfoDataSource = ExerciseInfoDataSource()
        )
    )
    private val _uiState : MutableStateFlow<ExerciseListState> = MutableStateFlow(ExerciseListState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        getData()
    }

    fun getData(){
        viewModelScope.launch {
            _uiState.emit(ExerciseListState.Loading)
            getExercisesUseCase.invoke().fold(
                onSuccess = { data ->
                    _uiState.emit(ExerciseListState.Content(data))
                },
                onFailure = { error ->
                    _uiState.emit(ExerciseListState.Error(error.message.toString()))
                }
            )
        }
    }
}