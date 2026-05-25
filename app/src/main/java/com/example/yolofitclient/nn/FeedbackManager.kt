package com.example.yolofitclient.nn


import android.content.Context
import android.speech.tts.TextToSpeech
import com.example.yolofitclient.domain.entity.TrackingConfigEntity
import java.util.*

class FeedbackManager(
    private val context: Context,
    private val config: TrackingConfigEntity
) {
    private var tts: TextToSpeech? = null
    private var lastFeedbackTime = 0L
    private var lastSpokenAdvice: String? = null
    private val minIntervalMs = 3000L
    private var currentPhase = ExerciseCounter.Phase.NONE
    private var minAngleInDown: Double = 180.0
    private var maxAngleInUp: Double = 0.0

    fun init() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale("ru")
        }
    }

    fun analyzeAndGiveFeedback(
        currentAngle: Double,
        detectedPhase: ExerciseCounter.Phase
    ): String? {
        val now = System.currentTimeMillis()

        // Находим экстремумы
        when (detectedPhase) {
            ExerciseCounter.Phase.DOWN -> {
                if (currentAngle < minAngleInDown) minAngleInDown = currentAngle
            }
            ExerciseCounter.Phase.UP -> {
                if (currentAngle > maxAngleInUp) maxAngleInUp = currentAngle
            }
            else -> {}
        }

        // Когда фаза меняется смотрим на предпоследнюю
        if (detectedPhase != currentPhase && now - lastFeedbackTime >= minIntervalMs) {
            val advice = when (currentPhase) {
                ExerciseCounter.Phase.DOWN -> {
                    if (minAngleInDown > config.angleDown + 5.0) {
                        config.bendHint ?: "Согнитесь сильнее угол был ${minAngleInDown.toInt()}, нужно <= ${config.angleDown.toInt()}"
                    } else null
                }
                ExerciseCounter.Phase.UP -> {
                    if (maxAngleInUp < config.angleUp - 5.0) {
                        config.straightenHint ?: "Выпрямитесь полностью угол был ${maxAngleInUp.toInt()}, нужно >= ${config.angleUp.toInt()}"
                    } else null
                }
                else -> null
            }

            // Сбросываем экстремумы
            minAngleInDown = 180.0
            maxAngleInUp = 0.0
            currentPhase = detectedPhase

            if (advice != null) {
                lastSpokenAdvice = advice
                lastFeedbackTime = now
                speak(advice)
                return advice
            }
        }

        return null
    }

    private fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "feedback_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}