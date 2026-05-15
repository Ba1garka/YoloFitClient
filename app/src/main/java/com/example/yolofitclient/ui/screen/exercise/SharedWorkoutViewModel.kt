package com.example.yolofitclient.ui.screen.exercise

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedWorkoutViewModel : ViewModel() {
    private val _selectedIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedIds: StateFlow<List<Int>> = _selectedIds.asStateFlow()

    fun setSelectedIds(ids: List<Int>) {
        _selectedIds.value = ids.toList()
    }

    fun clearSelection() {
        _selectedIds.value = emptyList()
    }
}