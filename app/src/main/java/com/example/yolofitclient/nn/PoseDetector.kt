package com.example.yolofitclient.nn


import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import ai.onnxruntime.*
import android.os.Build
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.exp

class PoseDetector(context: Context) {

    private var session: OrtSession? = null
    private var environment: OrtEnvironment? = null
    private val inputSize = 640
    private val numKeypoints = 17
    private val outputChannels = 56

    private val confThreshold = 0.6f
    private val iouThreshold = 0.5f
    private val maxDetections = 1

    companion object {
        val SKELETON = listOf(
            15 to 13, 13 to 11, 16 to 14, 14 to 12, 11 to 12,
            5 to 11, 6 to 12, 5 to 6, 5 to 7, 6 to 8,
            7 to 9, 8 to 10, 1 to 2, 0 to 1, 0 to 2,
            1 to 3, 2 to 4, 3 to 5, 4 to 6
        )
    }

    data class Keypoint(val x: Float, val y: Float, val conf: Float)
    data class Detection(
        val bbox: FloatArray,
        val keypoints: List<Keypoint>,
        val confidence: Float
    )

    init {
        try {
            val modelFile = copyModelFromAssets(context, "best2.onnx")
            environment = OrtEnvironment.getEnvironment()
            val sessionOptions = OrtSession.SessionOptions().apply {
                setOptimizationLevel(OrtSession.SessionOptions.OptLevel.ALL_OPT)
                setExecutionMode(OrtSession.SessionOptions.ExecutionMode.PARALLEL)
                setInterOpNumThreads(4)
                setIntraOpNumThreads(4)
            }

            session = environment?.createSession(modelFile.absolutePath, sessionOptions)

            session?.let { sess ->
                Log.d("PoseDetector", "Inputs: ${sess.inputInfo.keys}")
                Log.d("PoseDetector", "Outputs: ${sess.outputInfo.keys}")
                sess.outputInfo.values.firstOrNull()?.info?.let { info ->
                    if (info is TensorInfo) {
                        Log.d("PoseDetector", "Output shape: ${info.shape.joinToString()}")
                    }
                }
            }

            Log.d("PoseDetector", "ONNX модель загружена")
        } catch (e: Exception) {
            Log.e("PoseDetector", "Ошибка загрузки: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun copyModelFromAssets(context: Context, filename: String): File {
        val modelFile = File(context.filesDir, filename)
        if (modelFile.exists() && modelFile.length() > 0) return modelFile

        val inputStream: InputStream = context.assets.open(filename)
        val outputStream = FileOutputStream(modelFile)
        val buffer = ByteArray(8192)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.close()
        inputStream.close()
        return modelFile
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun detect(bitmap: Bitmap): List<Detection> {
        if (session == null || environment == null) {
            Log.e("PoseDetector", "Сессия или окружение null")
            return emptyList()
        }

        val startTime = System.currentTimeMillis()


        val resized = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true)
        val inputData = bitmapToFloatBuffer(resized)

        val tensor = OnnxTensor.createTensor(
            environment,
            inputData,
            longArrayOf(1, 3, inputSize.toLong(), inputSize.toLong())
        )

        val results = try {
            @Suppress("UNCHECKED_CAST")
            session?.run(mapOf("images" to tensor))
        } catch (e: Exception) {
            Log.e("PoseDetector", "Run error: ${e.message}")
            null
        }
        tensor.close()

        if (results == null) {
            resized.recycle()
            return emptyList()
        }

        var detections = emptyList<Detection>()

        try {
            val outputTensor = results["output0"]?.get() as? OnnxTensor
            if (outputTensor != null) {
                val floatBuffer = outputTensor.floatBuffer
                val size = floatBuffer.remaining()
                val output = FloatArray(size)
                floatBuffer.rewind()
                floatBuffer.get(output, 0, size)

                Log.d("PoseDetector", "Output size: $size, cells: ${size / outputChannels}")

                detections = parseOutput(output, size)
            }
        } catch (e: Exception) {
            Log.e("PoseDetector", "Ошибка парсинга: ${e.message}")
            e.printStackTrace()
        } finally {
            results.close()
            resized.recycle()
        }

        val inferenceTime = System.currentTimeMillis() - startTime
        Log.d("PoseDetector", "${inferenceTime}ms, найдено: ${detections.size}")

        return detections
    }

    private fun bitmapToFloatBuffer(bitmap: Bitmap): FloatBuffer {
        val buffer = ByteBuffer.allocateDirect(3 * inputSize * inputSize * 4)
        buffer.order(ByteOrder.nativeOrder())
        val floatBuffer = buffer.asFloatBuffer()

        val pixels = IntArray(inputSize * inputSize)
        bitmap.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        val rValues = FloatArray(inputSize * inputSize)
        val gValues = FloatArray(inputSize * inputSize)
        val bValues = FloatArray(inputSize * inputSize)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            rValues[i] = ((pixel shr 16) and 0xFF) / 255.0f
            gValues[i] = ((pixel shr 8) and 0xFF) / 255.0f
            bValues[i] = (pixel and 0xFF) / 255.0f
        }

        floatBuffer.put(rValues)
        floatBuffer.put(gValues)
        floatBuffer.put(bValues)
        floatBuffer.rewind()
        return floatBuffer
    }

//    выход модели (1, 56, 8400)
//    [0:4]  — bbox (cx, cy, w, h) — абсолютные координаты [0, 640]
//    [4]    — confidence [0, 1]
//    [5:56] — 17 ключевых точек × 3 (x, y, conf_logit)

    private fun parseOutput(output: FloatArray, size: Int): List<Detection> {
        val totalCells = size / outputChannels
        val rawDetections = mutableListOf<Detection>()

        var maxConf = -1f
        var maxConfIdx = -1

        for (i in 0 until totalCells) {

            val bboxCx = output[0 * totalCells + i]
            val bboxCy = output[1 * totalCells + i]
            val bboxW  = output[2 * totalCells + i]
            val bboxH  = output[3 * totalCells + i]
            val confidenceRaw = output[4 * totalCells + i]
            val confidence = sigmoid(confidenceRaw)

            if (confidence > maxConf) {
                maxConf = confidence
                maxConfIdx = i
            }

            if (confidence < confThreshold) continue

            val cx = bboxCx
            val cy = bboxCy
            val bw = bboxW
            val bh = bboxH

            val x1 = (cx - bw / 2f).coerceIn(0f, inputSize.toFloat())
            val y1 = (cy - bh / 2f).coerceIn(0f, inputSize.toFloat())
            val x2 = (cx + bw / 2f).coerceIn(0f, inputSize.toFloat())
            val y2 = (cy + bh / 2f).coerceIn(0f, inputSize.toFloat())

            val boxW = x2 - x1
            val boxH = y2 - y1

            if (boxW < 20f || boxH < 40f) continue
            if (boxW > 600f || boxH > 600f) continue

            val keypoints = mutableListOf<Keypoint>()
            var validKpCount = 0

            for (kpIdx in 0 until numKeypoints) {
                val kpChannel = 5 + kpIdx * 3
                val kpX = output[kpChannel * totalCells + i]
                val kpY = output[(kpChannel + 1) * totalCells + i]
                val kpConfLogit = output[(kpChannel + 2) * totalCells + i]
                val kpConf = sigmoid(kpConfLogit)

                if (kpX < -100f || kpX > inputSize + 100f ||
                    kpY < -100f || kpY > inputSize + 100f) {
                    keypoints.add(Keypoint(0f, 0f, 0f))
                    continue
                }

                val clampedX = kpX.coerceIn(0f, inputSize.toFloat())
                val clampedY = kpY.coerceIn(0f, inputSize.toFloat())

                if (kpConf > 0.4f) validKpCount++
                keypoints.add(Keypoint(clampedX, clampedY, kpConf))
            }

            if (validKpCount < 5) continue

            rawDetections.add(
                Detection(floatArrayOf(x1, y1, x2, y2), keypoints, confidence)
            )
        }


        if (maxConfIdx >= 0) {
            Log.d("PoseDetector", "MAX: idx=$maxConfIdx conf=$maxConf " +
                    "cx=${output[0 * totalCells + maxConfIdx]} " +
                    "cy=${output[1 * totalCells + maxConfIdx]}")
        }

        Log.d("PoseDetector", "Сырых детекций: ${rawDetections.size}")
        return applyNMS(rawDetections)
    }

    private fun applyNMS(detections: List<Detection>): List<Detection> {
        if (detections.isEmpty()) return emptyList()

        val sorted = detections.sortedByDescending { it.confidence }.toMutableList()
        val kept = mutableListOf<Detection>()

        while (sorted.isNotEmpty() && kept.size < maxDetections) {
            val best = sorted.removeAt(0)
            kept.add(best)

            val iterator = sorted.iterator()
            while (iterator.hasNext()) {
                val candidate = iterator.next()
                if (calculateIOU(best.bbox, candidate.bbox) > iouThreshold) {
                    iterator.remove()
                }
            }
        }

        for ((idx, det) in kept.withIndex()) {
            Log.d("PoseDetector", "NMS[$idx]: conf=${det.confidence} " +
                    "bbox=(${det.bbox[0]},${det.bbox[1]},${det.bbox[2]},${det.bbox[3]}) " +
                    "kp0=(${det.keypoints[0].x},${det.keypoints[0].y})")
        }

        Log.d("PoseDetector", "После NMS: ${kept.size}")
        return kept
    }

    private fun calculateIOU(boxA: FloatArray, boxB: FloatArray): Float {
        val xA = maxOf(boxA[0], boxB[0])
        val yA = maxOf(boxA[1], boxB[1])
        val xB = minOf(boxA[2], boxB[2])
        val yB = minOf(boxA[3], boxB[3])

        val interArea = maxOf(0f, xB - xA) * maxOf(0f, yB - yA)
        val boxAArea = (boxA[2] - boxA[0]) * (boxA[3] - boxA[1])
        val boxBArea = (boxB[2] - boxB[0]) * (boxB[3] - boxB[1])

        val union = boxAArea + boxBArea - interArea
        return if (union > 1e-6f) interArea / union else 0f
    }

    private fun sigmoid(x: Float): Float = 1f / (1f + exp(-x.toDouble())).toFloat()

    fun close() {
        try {
            session?.close()
            environment?.close()
        } catch (e: Exception) {
            Log.d("PoseDetector", "Close error: ${e.message}")
        }
    }
}