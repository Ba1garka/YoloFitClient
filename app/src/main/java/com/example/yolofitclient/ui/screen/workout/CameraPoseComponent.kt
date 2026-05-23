package com.example.yolofitclient.ui.screen.workout


import android.graphics.*
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.yolofitclient.domain.entity.TrackingConfig
import com.example.yolofitclient.domain.entity.TrackingConfigEntity
import com.example.yolofitclient.nn.ExerciseCounter
import com.example.yolofitclient.nn.PoseDetector
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@Composable
fun CameraPoseComponent(
    trackingConfig: TrackingConfigEntity,
    onRepsUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current

    val config = remember(trackingConfig) {
        TrackingConfig(
            jointTriplet = trackingConfig.jointIndices.split(",").map { it.trim().toInt() },
            angleDown = trackingConfig.angleDown,
            angleUp = trackingConfig.angleUp,
            countDirection = when (trackingConfig.countDirection) {
                "down_to_up" -> ExerciseCounter.Direction.DOWN_TO_UP
                "up_to_down" -> ExerciseCounter.Direction.UP_TO_DOWN
                else -> ExerciseCounter.Direction.HOLD
            },
            minConfidence = trackingConfig.minConfidence.toFloat(),
            framesToConfirm = trackingConfig.framesToConfirm
        )
    }

    val detector = remember {
        try {
            PoseDetector(context)
        } catch (e: Exception) {
            Log.e("CameraPose", "Model error: ${e.message}")
            null
        }
    }

    var exerciseCounter by remember { mutableStateOf(ExerciseCounter(config)) }


    LaunchedEffect(config) {
        exerciseCounter = ExerciseCounter(config)
    }

    var detections by remember { mutableStateOf(emptyList<PoseDetector.Detection>()) }
    var fps by remember { mutableFloatStateOf(0f) }
    var frameCount by remember { mutableIntStateOf(0) }
    var lastFpsTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var smoothedDetections by remember { mutableStateOf(emptyList<PoseDetector.Detection>()) }

    DisposableEffect(lifecycle) {
        onDispose {
            detector?.close()
        }
    }

    if (detector == null) {
        Box(modifier = modifier, contentAlignment = androidx.compose.ui.Alignment.Center) {
            Text("Ошибка загрузки модели", color = Color.White, fontSize = 14.sp)
        }
        return
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { viewContext ->
                val previewView = PreviewView(viewContext)
                val cameraProvider = ProcessCameraProvider.getInstance(viewContext)

                cameraProvider.addListener({
                    try {
                        val camProvider = cameraProvider.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val analyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setTargetResolution(Size(640, 640))
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                            .build()

                        analyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                            try {
                                val bitmap = imageProxyToBitmap(imageProxy)
                                if (bitmap != null) {
                                    val result = detector.detect(bitmap)

                                    smoothedDetections = if (result.isNotEmpty() && smoothedDetections.isNotEmpty()) {
                                        val alpha = 0.85f
                                        result.mapIndexed { i, det ->
                                            if (i < smoothedDetections.size) {
                                                val prev = smoothedDetections[i]
                                                PoseDetector.Detection(
                                                    bbox = floatArrayOf(
                                                        alpha * det.bbox[0] + (1 - alpha) * prev.bbox[0],
                                                        alpha * det.bbox[1] + (1 - alpha) * prev.bbox[1],
                                                        alpha * det.bbox[2] + (1 - alpha) * prev.bbox[2],
                                                        alpha * det.bbox[3] + (1 - alpha) * prev.bbox[3]
                                                    ),
                                                    keypoints = det.keypoints.mapIndexed { j, kp ->
                                                        if (j < prev.keypoints.size) {
                                                            PoseDetector.Keypoint(
                                                                alpha * kp.x + (1 - alpha) * prev.keypoints[j].x,
                                                                alpha * kp.y + (1 - alpha) * prev.keypoints[j].y,
                                                                kp.conf
                                                            )
                                                        } else kp
                                                    },
                                                    confidence = det.confidence
                                                )
                                            } else det
                                        }
                                    } else result

                                    detections = smoothedDetections
                                    exerciseCounter.update(detections)
                                    onRepsUpdate(exerciseCounter.getCount())

                                    bitmap.recycle()
                                }

                                frameCount++
                                val now = System.currentTimeMillis()
                                if (now - lastFpsTime >= 1000) {
                                    fps = frameCount * 1000f / (now - lastFpsTime)
                                    frameCount = 0
                                    lastFpsTime = now
                                }
                            } catch (e: Exception) {
                                Log.e("CameraPose", "Error: ${e.message}")
                            } finally {
                                imageProxy.close()
                            }
                        }

                        camProvider.unbindAll()
                        camProvider.bindToLifecycle(
                            lifecycle,
                            CameraSelector.DEFAULT_FRONT_CAMERA,
                            preview,
                            analyzer
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPose", "Camera error: ${e.message}")
                    }
                }, ContextCompat.getMainExecutor(viewContext))

                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val vw = size.width
            val vh = size.height

            if (detections.isNotEmpty()) {
                val colors = listOf(
                    Color(0xFFFF0000), Color(0xFFFF6D00), Color(0xFFFFD600),
                    Color(0xFF00FF00), Color(0xFF00BFFF), Color(0xFF0000FF), Color(0xFF8B00FF)
                )

                for (det in detections) {

                    val bx = det.bbox[0] * vw / 640f
                    val by = det.bbox[1] * vh / 640f
                    val bw = (det.bbox[2] - det.bbox[0]) * vw / 640f
                    val bh = (det.bbox[3] - det.bbox[1]) * vh / 640f
                    drawRect(
                        Color.Green.copy(alpha = 0.3f),
                        Offset(bx, by),
                        androidx.compose.ui.geometry.Size(bw, bh),
                        style = Stroke(2f)
                    )

                    PoseDetector.SKELETON.forEachIndexed { idx, conn ->
                        val k1 = det.keypoints.getOrNull(conn.first) ?: return@forEachIndexed
                        val k2 = det.keypoints.getOrNull(conn.second) ?: return@forEachIndexed
                        if (k1.conf > 0.4f && k2.conf > 0.4f) {
                            drawLine(
                                colors[idx % colors.size],
                                Offset(k1.x * vw / 640f, k1.y * vh / 640f),
                                Offset(k2.x * vw / 640f, k2.y * vh / 640f),
                                strokeWidth = 3f
                            )
                        }
                    }

                    det.keypoints.forEach { kp ->
                        if (kp.conf > 0.4f) {
                            drawCircle(
                                Color.White, 5f,
                                Offset(kp.x * vw / 640f, kp.y * vh / 640f)
                            )
                            drawCircle(
                                Color.Red, 3f,
                                Offset(kp.x * vw / 640f, kp.y * vh / 640f)
                            )
                        }
                    }
                }
            }

            val count = exerciseCounter.getCount()
            val phase = exerciseCounter.getPhase()
            val angle = exerciseCounter.getAngle()

            drawContext.canvas.nativeCanvas.apply {
                val paintBg = Paint().apply {
                    color = android.graphics.Color.argb(180, 0, 0, 0)
                    style = Paint.Style.FILL
                }
                val paintText = Paint().apply {
                    color = android.graphics.Color.rgb(178, 234, 27)
                    textSize = 80f
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                val paintSub = Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 30f
                    isAntiAlias = true
                }

                val x = 30f
                val y = size.height - 100f
                drawRect(x - 10f, y - 70f, x + 300f, y + 50f, paintBg)
                drawText("$count", x, y, paintText)
                drawText(
                    "${"%.0f".format(angle)} ${
                        when (phase) {
                            ExerciseCounter.Phase.DOWN -> "ВНИЗ"
                            ExerciseCounter.Phase.UP -> "ВВЕРХ"
                            else -> "—"
                        }
                    }",
                    x, y + 40, paintSub
                )
            }

            drawContext.canvas.nativeCanvas.apply {
                val paint = Paint().apply {
                    color = android.graphics.Color.argb(150, 255, 255, 255)
                    textSize = 24f
                    isAntiAlias = true
                }
                drawText("FPS: ${"%.0f".format(fps)}", 20f, 40f, paint)
            }
        }
    }
}

private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
    return try {
        val planes = image.planes
        val yPlane = planes[0]
        val uPlane = planes[1]
        val vPlane = planes[2]

        val yBuffer = yPlane.buffer
        val uBuffer = uPlane.buffer
        val vBuffer = vPlane.buffer

        val width = image.width
        val height = image.height

        val nv21 = ByteArray(width * height * 3 / 2)
        var pos = 0

        for (row in 0 until height) {
            yBuffer.position(row * yPlane.rowStride)
            yBuffer.get(nv21, pos, width)
            pos += width
        }

        val uvWidth = width / 2
        val uvHeight = height / 2

        for (row in 0 until uvHeight) {
            val vPos = row * vPlane.rowStride
            val uPos = row * uPlane.rowStride

            for (col in 0 until uvWidth) {
                nv21[pos++] = vBuffer.get(vPos + col * vPlane.pixelStride)
                nv21[pos++] = uBuffer.get(uPos + col * uPlane.pixelStride)
            }
        }

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
        val imageBytes = out.toByteArray()
        out.close()

        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val matrix = Matrix().apply {
            postRotate(270f)
            postScale(-1f, 1f, width / 2f, height / 2f)
        }

        bitmap?.let {
            val rotated = Bitmap.createBitmap(it, 0, 0, it.width, it.height, matrix, true)
            it.recycle()
            val scaled = Bitmap.createScaledBitmap(rotated, 640, 640, true)
            rotated.recycle()
            scaled
        }
    } catch (e: Exception) {
        Log.e("CameraPose", "Ошибка конвертации: ${e.message}")
        null
    }
}