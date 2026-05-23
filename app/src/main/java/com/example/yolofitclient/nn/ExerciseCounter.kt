package com.example.yolofitclient.nn

import android.util.Log
import com.example.yolofitclient.domain.entity.TrackingConfig
import kotlin.math.acos
import kotlin.math.sqrt



class ExerciseCounter(private val config: TrackingConfig) {

    enum class Phase { NONE, DOWN, UP }

    enum class Direction { DOWN_TO_UP, UP_TO_DOWN, HOLD }

    private var repCount = 0
    private var currentPhase = Phase.NONE
    private var phaseFrameCount = 0
    private var lastAngle: Double = 180.0

    fun getCount(): Int = repCount
    fun getPhase(): Phase = currentPhase
    fun getAngle(): Double = lastAngle

    fun update(detections: List<PoseDetector.Detection>) {
        if (detections.isEmpty()) {
            phaseFrameCount = 0
            return
        }

        val det = detections[0]
        val kps = det.keypoints

        val (idxP1, idxP2, idxP3) = config.jointTriplet

        val p1 = kps.getOrNull(idxP1)?.takeIf { it.conf > config.minConfidence }
        val p2 = kps.getOrNull(idxP2)?.takeIf { it.conf > config.minConfidence }
        val p3 = kps.getOrNull(idxP3)?.takeIf { it.conf > config.minConfidence }

        if (p1 == null || p2 == null || p3 == null) {
            phaseFrameCount = 0
            return
        }

        lastAngle = calculateAngle(p1, p2, p3)

        if (phaseFrameCount % 10 == 0) {
            Log.d("ExerciseCounter", "Угол: ${"%.1f".format(lastAngle)} | Фаза: $currentPhase | Пороги: ≤${config.angleDown} DOWN, ≥${config.angleUp} UP")
        }

        if (config.countDirection == Direction.HOLD) {
            currentPhase = if (lastAngle in config.angleDown..config.angleUp) Phase.UP else Phase.DOWN
            return
        }

        val newPhase = when {
            lastAngle <= config.angleDown -> Phase.DOWN
            lastAngle >= config.angleUp -> Phase.UP
            else -> currentPhase
        }

        if (newPhase == currentPhase) {
            phaseFrameCount++
        } else {
            if (phaseFrameCount >= config.framesToConfirm &&
                currentPhase == Phase.DOWN && newPhase == Phase.UP &&
                config.countDirection == Direction.DOWN_TO_UP) {
                repCount++
                Log.d("ExerciseCounter", "Повторение! Всего: $repCount")
            }
            if (phaseFrameCount >= config.framesToConfirm &&
                currentPhase == Phase.UP && newPhase == Phase.DOWN &&
                config.countDirection == Direction.UP_TO_DOWN) {
                repCount++
                Log.d("ExerciseCounter", "Повторение! Всего: $repCount")
            }
            currentPhase = newPhase
            phaseFrameCount = 1
        }
    }

    private fun calculateAngle(
        p1: PoseDetector.Keypoint,
        p2: PoseDetector.Keypoint,
        p3: PoseDetector.Keypoint
    ): Double {
        val v1x = p1.x - p2.x
        val v1y = p1.y - p2.y
        val v2x = p3.x - p2.x
        val v2y = p3.y - p2.y
        val dot = v1x * v2x + v1y * v2y
        val norm1 = sqrt((v1x * v1x + v1y * v1y).toDouble())
        val norm2 = sqrt((v2x * v2x + v2y * v2y).toDouble())
        if (norm1 == 0.0 || norm2 == 0.0) return 180.0
        val cos = (dot / (norm1 * norm2)).coerceIn(-1.0, 1.0)
        return Math.toDegrees(acos(cos))
    }
}