package com.example.yolofitclient.domain.entity

import com.example.yolofitclient.nn.ExerciseCounter


data class TrackingConfig(
    val jointTriplet: List<Int>,
    val angleDown: Double,
    val angleUp: Double,
    val countDirection: ExerciseCounter.Direction,
    val minConfidence: Float = 0.4f,
    val framesToConfirm: Int = 3
)