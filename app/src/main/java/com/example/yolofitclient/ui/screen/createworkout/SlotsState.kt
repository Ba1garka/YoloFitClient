package com.example.yolofitclient.ui.screen.createworkout

import com.example.yolofitclient.domain.entity.TimeSlotEntity

sealed interface SlotsState {
    data class Error( val reason: String ): SlotsState
    data object Loading: SlotsState
    data class Content(
        val timeSlots: List<TimeSlotEntity>
    ): SlotsState
}