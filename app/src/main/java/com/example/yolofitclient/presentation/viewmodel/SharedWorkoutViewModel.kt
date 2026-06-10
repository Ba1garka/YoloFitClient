package com.example.yolofitclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedWorkoutViewModel : ViewModel() {
    private val _selectedIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedIds: StateFlow<List<Int>> = _selectedIds.asStateFlow()

    private val _selectedWorkoutId = MutableStateFlow<Int?>(null)
    val selectedWorkoutId: StateFlow<Int?> = _selectedWorkoutId.asStateFlow()

    fun setSelectedIds(ids: List<Int>) {
        _selectedIds.value = ids.toList()
    }

    fun setWorkoutId(id: Int) {
        _selectedWorkoutId.value = id
    }

    fun clearSelection() {
        _selectedIds.value = emptyList()
    }

    fun clearWorkoutId() {
        _selectedWorkoutId.value = null
    }

}