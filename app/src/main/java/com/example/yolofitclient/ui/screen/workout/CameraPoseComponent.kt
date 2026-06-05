package com.example.yolofitclient.ui.screen.workout


import android.graphics.*
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.yolofitclient.domain.entity.TrackingConfig
import com.example.yolofitclient.domain.entity.TrackingConfigEntity
import com.example.yolofitclient.nn.ExerciseCounter
import com.example.yolofitclient.nn.PoseDetector
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import kotlin.math.acos
import kotlin.math.sqrt

@Composable
fun CameraPoseComponent(
    trackingConfig: TrackingConfigEntity,
    onRepsUpdate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onAiFeedback: (angle: Double, phase: ExerciseCounter.Phase) -> Unit ,
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

    var previousCount by remember { mutableIntStateOf(0) }
    var pulse by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (pulse) 1.3f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { pulse = false }
    )


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
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("Ошибка загрузки модели", color = Color.White, fontSize = 14.sp)
        }
        return
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { viewContext ->
                val previewView = PreviewView(viewContext) //окно
                val cameraProvider = ProcessCameraProvider.getInstance(viewContext)

                cameraProvider.addListener({
                    try {
                        val camProvider = cameraProvider.get()

                        val preview = Preview.Builder().build().also { //генерирует поток
                            it.setSurfaceProvider(previewView.surfaceProvider) //проводник видеопотока
                        }

                        val analyzer = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setTargetResolution(Size(640, 640))
                            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                            .build()

                        analyzer.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy -> //кадр с камеры
                            try {
                                val bitmap = imageProxyToBitmap(imageProxy)
                                if (bitmap != null) {
                                    val result = detector.detect(bitmap)

                                    val rawAngle = if (result.isNotEmpty()) {
                                        val first = result[0]
                                        val kps = first.keypoints
                                        val (i1, i2, i3) = config.jointTriplet
                                        val p1 = kps.getOrNull(i1)?.takeIf { it.conf > config.minConfidence }
                                        val p2 = kps.getOrNull(i2)?.takeIf { it.conf > config.minConfidence }
                                        val p3 = kps.getOrNull(i3)?.takeIf { it.conf > config.minConfidence }
                                        if (p1 != null && p2 != null && p3 != null) {
                                            calculateAngle(p1, p2, p3)
                                        } else null
                                    } else null

                                    val instantPhase = when {
                                        rawAngle != null && rawAngle <= config.angleDown -> ExerciseCounter.Phase.DOWN
                                        rawAngle != null && rawAngle >= config.angleUp -> ExerciseCounter.Phase.UP
                                        else -> null
                                    }

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

                                    detections = smoothedDetections //рекомпозиция
                                    exerciseCounter.update(detections)
                                    onRepsUpdate(exerciseCounter.getCount())

                                    if (rawAngle != null && instantPhase != null) {
                                        onAiFeedback(rawAngle, instantPhase)
                                    }

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

            val currentCount = exerciseCounter.getCount()
            if (currentCount > previousCount) {
                pulse = true
                previousCount = currentCount
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

        val currentCount = exerciseCounter.getCount()
        LaunchedEffect(currentCount) {
            if (currentCount > previousCount) {
                pulse = true
                previousCount = currentCount
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.size(80.dp).scale(scale).clip(CircleShape).background(Color(0xCC000000))
                        .border(2.dp, Color(0xFFB2EA1B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "$currentCount", color = Color(0xFFB2EA1B), fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))

                val phase = exerciseCounter.getPhase()
                val angle = exerciseCounter.getAngle()
                Text(
                    text = "${"%.0f".format(angle)}° ${
                        when (phase) {
                            ExerciseCounter.Phase.DOWN -> "ВНИЗ"
                            ExerciseCounter.Phase.UP -> "ВВЕРХ"
                            else -> "—"
                        }
                    }",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.background(Color(0xAA000000), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
                )
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

private fun calculateAngle(p1: PoseDetector.Keypoint, p2: PoseDetector.Keypoint, p3: PoseDetector.Keypoint): Double {
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