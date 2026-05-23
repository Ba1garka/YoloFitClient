package com.example.yolofitclient.domain.entity

data class TrackingConfigEntity(
    val id: Int,
    val exerciseId: Int,
    val exerciseName: String,
    val jointIndices: String,
    val angleDown: Double,
    val angleUp: Double,
    val countDirection: String,
    val minConfidence: Double,
    val framesToConfirm: Int,
    val description: String?
)