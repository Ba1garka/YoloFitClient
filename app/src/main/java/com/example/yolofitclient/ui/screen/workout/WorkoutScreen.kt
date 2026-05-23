package com.example.yolofitclient.ui.screen.workout

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.data.dto.ExerciseSetDto
import com.example.yolofitclient.domain.entity.ExerciseEntity
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape

object WorkoutScreenColors {
    val DarkBackground = Color(0xFF0D0E0D)
    val CardBackground = Color(0xFF1A1F1A)
    val CardBorder = Color(0xFF2A3A2A)
    val AccentGreen = Color(0xFFB2EA1B)
    val AccentGreenDark = Color(0xFF2AC274)
    val AccentGreenLight = Color(0xFFC4FF66)
    val TextPrimary = Color(0xFFF0F0F0)
    val TextSecondary = Color(0xFFB0B0B0)
    val TextDim = Color(0xFF808080)
    val AccentOrange = Color(0xFFFF6D00)
    val ErrorRed = Color(0xFFFF4444)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    workoutId: Int,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    viewModel: WorkoutViewModel = viewModel { WorkoutViewModel(workoutId) }
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is WorkoutState.Success) {
            Toast.makeText(context, "Тренировка завершена! ", Toast.LENGTH_SHORT).show()
            onFinish()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(WorkoutScreenColors.DarkBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WorkoutScreenColors.AccentGreen.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.7f, size.height * 0.1f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width * 0.7f, size.height * 0.1f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        WorkoutScreenColors.AccentGreenDark.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.3f, size.height * 0.9f),
                    radius = size.minDimension * 0.5f
                ),
                radius = size.minDimension * 0.5f,
                center = Offset(size.width * 0.3f, size.height * 0.9f)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {

            WorkoutTopBar(
                state = state,
                onBack = onBack,
                onToggleAi = { viewModel.toggleAiMode() }
            )

            when (val currentState = state) {
                is WorkoutState.Loading -> LoadingContent()
                is WorkoutState.Error -> ErrorContent(
                    message = currentState.reason,
                    onRetry = { viewModel.retry() }
                )
                is WorkoutState.Content -> ContentState(
                    state = currentState,
                    onSelectExercise = { viewModel.selectExercise(it) },
                    onIncrementReps = { viewModel.incrementReps() },
                    onDecrementReps = { viewModel.decrementReps() },
                    onWeightChange = { viewModel.updateWeight(it) },
                    onCompleteSet = { viewModel.completeSet() },
                    onFinishWorkout = { viewModel.finishWorkout() },
                    viewModel = viewModel,
                    onMarkExerciseCompleted = { viewModel.markExerciseCompleted() } ,
                )
                is WorkoutState.Success -> SuccessContent()
            }
        }
    }
}

@Composable
private fun WorkoutTopBar(
    state: WorkoutState,
    onBack: () -> Unit,
    onToggleAi: () -> Unit
) {
    val isAiMode = state is WorkoutState.Content && state.isAiMode
    val isSubmitting = state is WorkoutState.Content && state.isSubmitting

    Surface(
        color = WorkoutScreenColors.CardBackground.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    "Назад",
                    tint = WorkoutScreenColors.AccentGreen
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "ТРЕНИРОВКА",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = WorkoutScreenColors.TextPrimary,
                        letterSpacing = 2.sp
                    )
                )
                if (state is WorkoutState.Content) {
                    Text(
                        "${state.exercises.size} упражнений",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WorkoutScreenColors.AccentGreen
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isAiMode) WorkoutScreenColors.AccentGreen.copy(alpha = 0.15f)
                        else WorkoutScreenColors.CardBackground
                    )
                    .border(
                        1.dp,
                        if (isAiMode) WorkoutScreenColors.AccentGreen
                        else WorkoutScreenColors.CardBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable(enabled = !isSubmitting) { onToggleAi() }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        if (isAiMode) Icons.Default.Videocam else Icons.Default.VideocamOff,
                        "AI",
                        tint = if (isAiMode) WorkoutScreenColors.AccentGreen
                        else WorkoutScreenColors.TextDim,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "AI",
                        color = if (isAiMode) WorkoutScreenColors.AccentGreen
                        else WorkoutScreenColors.TextDim,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentState(
    state: WorkoutState.Content,
    onSelectExercise: (Int) -> Unit,
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
    onWeightChange: (String) -> Unit,
    onCompleteSet: () -> Unit,
    onFinishWorkout: () -> Unit,
    viewModel: WorkoutViewModel,
    onMarkExerciseCompleted: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ExerciseSelector(
            exercises = state.exercises,
            selectedIndex = state.currentExerciseIndex,
            completedExercises = state.completedExercises,
            onSelect = onSelectExercise
        )

        if (state.isAiMode && state.currentExercise?.trackingConfig != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(WorkoutScreenColors.CardBackground)
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                WorkoutScreenColors.AccentGreen.copy(alpha = 0.5f),
                                WorkoutScreenColors.AccentGreenDark.copy(alpha = 0.3f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        ),
                        RoundedCornerShape(24.dp)
                    )
            ) {
                CameraPoseComponent(
                    trackingConfig = state.currentExercise!!.trackingConfig!!,
                    onRepsUpdate = { reps ->
                        viewModel.updateAiReps(reps)
                    },
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp))
                )
            }
        } else {
            ManualMode(
                state = state,
                onIncrementReps = onIncrementReps,
                onDecrementReps = onDecrementReps,
                onWeightChange = onWeightChange,
                onCompleteSet = onCompleteSet,
                modifier = Modifier.weight(1f),
                onMarkExerciseCompleted = onMarkExerciseCompleted,
            )
        }

        Log.d("WorkoutScreen" , "${state.exercises.isNotEmpty()} | ${state.completedExercises.size} | ${state.exercises.size}")

        if (state.allExercisesDone) {
            FinishButton(
                isSubmitting = state.isSubmitting,
                onFinish = onFinishWorkout,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ExerciseSelector(
    exercises: List<ExerciseEntity>,
    selectedIndex: Int,
    completedExercises: Set<Int>,
    onSelect: (Int) -> Unit
) {
    Surface(
        color = WorkoutScreenColors.CardBackground.copy(alpha = 0.7f),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            exercises.forEachIndexed { index, exercise ->
                val isSelected = index == selectedIndex
                val isCompleted = completedExercises.contains(exercise.id)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            when {
                                isSelected -> Brush.linearGradient(
                                    colors = listOf(
                                        WorkoutScreenColors.AccentGreen.copy(alpha = 0.25f),
                                        WorkoutScreenColors.AccentGreenDark.copy(alpha = 0.15f)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, Float.POSITIVE_INFINITY)
                                )
                                isCompleted -> SolidColor(WorkoutScreenColors.AccentGreenDark.copy(alpha = 0.15f))
                                else -> SolidColor(WorkoutScreenColors.CardBackground)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isSelected -> WorkoutScreenColors.AccentGreen
                                isCompleted -> WorkoutScreenColors.AccentGreenDark.copy(alpha = 0.5f)
                                else -> WorkoutScreenColors.CardBorder
                            },
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelect(index) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (isCompleted) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = WorkoutScreenColors.AccentGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            exercise.name,
                            color = when {
                                isSelected -> WorkoutScreenColors.AccentGreen
                                isCompleted -> WorkoutScreenColors.TextSecondary
                                else -> WorkoutScreenColors.TextDim
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ManualMode(
    state: WorkoutState.Content,
    onIncrementReps: () -> Unit,
    onDecrementReps: () -> Unit,
    onWeightChange: (String) -> Unit,
    onCompleteSet: () -> Unit,
    modifier: Modifier = Modifier,
    onMarkExerciseCompleted: () -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CurrentExerciseCard(exercise = state.currentExercise, completedSets = state.currentExerciseSets)
        }

        if (!state.isCurrentExerciseDone) {
            item {
                RepsCounter(
                    reps = state.currentReps,
                    onIncrement = onIncrementReps,
                    onDecrement = onDecrementReps
                )
            }

            item {
                WeightInput(
                    weight = state.currentWeight,
                    onWeightChange = onWeightChange
                )
            }

            item {
                CompleteSetButton(
                    enabled = state.currentReps > 0 && !state.isSubmitting,
                    onClick = onCompleteSet
                )
            }
        }

        if (state.hasEnoughSets && !state.isCurrentExerciseDone) {
            item {
                MarkExerciseCompletedButton(
                    onClick = onMarkExerciseCompleted
                )
            }
        }

        if (state.isCurrentExerciseDone) {
            item {
                ExerciseCompletedCard()
            }
        }

        if (state.completedSets.isNotEmpty()) {
            item {
                Text(
                    "ВЫПОЛНЕННЫЕ ПОДХОДЫ",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = WorkoutScreenColors.AccentGreen,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(
                state.completedSets.filter {
                    it.exerciseId.toInt() == state.currentExercise?.id
                }
            ) { set ->
                CompletedSetCard(set = set)
            }
        }
    }
}

@Composable
private fun CurrentExerciseCard(
    exercise: ExerciseEntity?,
    completedSets: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = DiagonalRoundedCornerShape(
            topLeft = 40f,
            topRight = 16f,
            bottomRight = 40f,
            bottomLeft = 16f
        ),
        colors = CardDefaults.cardColors(containerColor = WorkoutScreenColors.CardBackground),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    WorkoutScreenColors.AccentGreen.copy(alpha = 0.3f),
                    WorkoutScreenColors.CardBorder
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Column {
                Text(
                    exercise?.name ?: "—",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = WorkoutScreenColors.TextPrimary,
                        fontSize = 24.sp
                    )
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (exercise != null) {
                        Text(
                            "Подходы: $completedSets / ${exercise.defaultSets}",
                            color = if (completedSets >= exercise.defaultSets)
                                WorkoutScreenColors.AccentGreen
                            else WorkoutScreenColors.TextDim,
                            fontSize = 13.sp
                        )
                        Text(
                            "Повторы: ${exercise.defaultReps}",
                            color = WorkoutScreenColors.TextDim,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RepsCounter(
    reps: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WorkoutScreenColors.CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkoutScreenColors.CardBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "ПОВТОРЕНИЯ",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = WorkoutScreenColors.TextDim,
                    letterSpacing = 2.sp
                )
            )
            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(WorkoutScreenColors.AccentGreen.copy(alpha = 0.1f))
                        .border(1.dp, WorkoutScreenColors.AccentGreen.copy(alpha = 0.3f), CircleShape)
                        .clickable { onDecrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Remove,
                        null,
                        tint = WorkoutScreenColors.AccentGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "$reps",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = WorkoutScreenColors.AccentGreen,
                        fontSize = 64.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    WorkoutScreenColors.AccentGreen,
                                    WorkoutScreenColors.AccentGreenDark
                                ),
                                start = Offset(0f, 0f),
                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                            )
                        )
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        null,
                        tint = WorkoutScreenColors.DarkBackground,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeightInput(
    weight: String,
    onWeightChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WorkoutScreenColors.CardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkoutScreenColors.CardBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(WorkoutScreenColors.AccentGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    null,
                    tint = WorkoutScreenColors.AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                "Вес (кг):",
                color = WorkoutScreenColors.TextSecondary,
                fontSize = 16.sp
            )
            Spacer(Modifier.weight(1f))

            OutlinedTextField(
                value = weight,
                onValueChange = onWeightChange,
                modifier = Modifier.width(100.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = WorkoutScreenColors.TextPrimary,
                    textAlign = TextAlign.Center
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = WorkoutScreenColors.AccentGreen,
                    unfocusedBorderColor = WorkoutScreenColors.CardBorder,
                    cursorColor = WorkoutScreenColors.AccentGreen
                )
            )
        }
    }
}

@Composable
private fun CompleteSetButton(
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (enabled) {
                        Brush.linearGradient(
                            colors = listOf(
                                WorkoutScreenColors.AccentGreen,
                                WorkoutScreenColors.AccentGreenDark
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    } else {
                        SolidColor(WorkoutScreenColors.CardBorder)
                    },
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = if (enabled) WorkoutScreenColors.DarkBackground
                    else WorkoutScreenColors.TextDim
                )
                Text(
                    "Завершить подход",
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) WorkoutScreenColors.DarkBackground
                    else WorkoutScreenColors.TextDim,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun CompletedSetCard(set: ExerciseSetDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = WorkoutScreenColors.AccentGreenDark.copy(alpha = 0.08f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            WorkoutScreenColors.AccentGreen.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = WorkoutScreenColors.AccentGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    "Подход ${set.setNumber}",
                    color = WorkoutScreenColors.AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "${set.repsDone ?: 0} повт",
                    color = WorkoutScreenColors.TextPrimary,
                    fontSize = 14.sp
                )
                if (set.weightDone != null) {
                    Text(
                        "${set.weightDone} кг",
                        color = WorkoutScreenColors.TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FinishButton(
    isSubmitting: Boolean,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onFinish,
        enabled = !isSubmitting,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = WorkoutScreenColors.AccentOrange.copy(alpha = 0.15f),
            disabledContainerColor = WorkoutScreenColors.CardBorder
        )
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = WorkoutScreenColors.AccentOrange
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Flag,
                    null,
                    tint = WorkoutScreenColors.AccentOrange
                )
                Text(
                    "Завершить тренировку",
                    color = WorkoutScreenColors.AccentOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = WorkoutScreenColors.AccentGreen)
            Text(
                "Загрузка тренировки...",
                color = WorkoutScreenColors.TextSecondary
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                null,
                tint = WorkoutScreenColors.ErrorRed,
                modifier = Modifier.size(48.dp)
            )
            Text(
                message,
                color = WorkoutScreenColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WorkoutScreenColors.AccentGreenDark
                )
            ) {
                Text("Повторить")
            }
        }
    }
}

@Composable
private fun SuccessContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.EmojiEvents,
                null,
                tint = WorkoutScreenColors.AccentGreen,
                modifier = Modifier.size(80.dp)
            )
            Text(
                "Тренировка завершена!",
                color = WorkoutScreenColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                "Отличная работа!",
                color = WorkoutScreenColors.TextSecondary,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun MarkExerciseCompletedButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            WorkoutScreenColors.AccentGreen,
                            WorkoutScreenColors.AccentGreenDark
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                    ),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.NavigateNext,
                    null,
                    tint = WorkoutScreenColors.DarkBackground
                )
                Text(
                    "Завершить упражнение",
                    fontWeight = FontWeight.Bold,
                    color = WorkoutScreenColors.DarkBackground,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ExerciseCompletedCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = WorkoutScreenColors.AccentGreenDark.copy(alpha = 0.15f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            WorkoutScreenColors.AccentGreen.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                null,
                tint = WorkoutScreenColors.AccentGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "Упражнение завершено!",
                color = WorkoutScreenColors.AccentGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}