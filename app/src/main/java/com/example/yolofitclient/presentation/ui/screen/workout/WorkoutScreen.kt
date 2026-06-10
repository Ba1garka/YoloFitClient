package com.example.yolofitclient.presentation.ui.screen.workout

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.data.dto.ExerciseSetDto
import com.example.yolofitclient.domain.entity.ExerciseEntity
import kotlinx.coroutines.delay
import com.example.yolofitclient.R
import com.example.yolofitclient.presentation.ui.theme.DiagonalRoundedCornerShape
import com.example.yolofitclient.presentation.ui.theme.ExerciseColors
import com.example.yolofitclient.presentation.viewmodel.WorkoutViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    workoutId: Int,
    onFinish: () -> Unit,
    onBack: () -> Unit,
    viewModel: WorkoutViewModel = viewModel { WorkoutViewModel(workoutId) }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(state) {
        if (state is WorkoutState.Success) {
            Toast.makeText(context, "Тренировка завершена! ", Toast.LENGTH_SHORT).show()
            onFinish()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(ExerciseColors.DarkBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        ExerciseColors.AccentGreen.copy(alpha = 0.1f),
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
                        ExerciseColors.AccentGreenDark.copy(alpha = 0.06f),
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
        color = ExerciseColors.CardBackground.copy(alpha = 0.95f),
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
                    tint = ExerciseColors.AccentGreen
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.workout),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = ExerciseColors.TextPrimary,
                        letterSpacing = 2.sp
                    )
                )
                if (state is WorkoutState.Content) {
                    Text(
                        "${state.exercises.size} упражнений",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ExerciseColors.AccentGreen
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isAiMode) ExerciseColors.AccentGreen.copy(alpha = 0.15f)
                        else ExerciseColors.CardBackground
                    )
                    .border(
                        1.dp,
                        if (isAiMode) ExerciseColors.AccentGreen
                        else ExerciseColors.CardBorder,
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
                        tint = if (isAiMode) ExerciseColors.AccentGreen
                        else ExerciseColors.TextDim,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        stringResource(R.string.ai),
                        color = if (isAiMode) ExerciseColors.AccentGreen else ExerciseColors.TextDim,
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

    val isResting by viewModel.isResting.collectAsStateWithLifecycle()
    val restSeconds by viewModel.restSeconds.collectAsStateWithLifecycle()

    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        ExerciseSelector(
            exercises = state.exercises,
            selectedIndex = state.currentExerciseIndex,
            completedExercises = state.completedExercises,
            onSelect = onSelectExercise
        )

        if (state.isAiMode && state.currentExercise?.trackingConfig != null) {

            LaunchedEffect(state.currentExercise?.trackingConfig?.id) {
                state.currentExercise?.trackingConfig?.let { config ->
                    viewModel.initAiSession(context, config)
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(ExerciseColors.CardBackground)
                    .border(
                        1.dp,
                        Brush.linearGradient(
                            colors = listOf(
                                ExerciseColors.AccentGreen.copy(alpha = 0.5f),
                                ExerciseColors.AccentGreenDark.copy(alpha = 0.3f)
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
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)),
                    onAiFeedback = { angle, phase ->
                        viewModel.processAiFeedback( angle, phase)
                    },
                    isResting
                )

                if (isResting) {
                    val animatedRest by animateIntAsState(
                        targetValue = restSeconds,
                        animationSpec = tween(durationMillis = 500)
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$animatedRest",
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFB2EA1B)
                        )
                    }
                }
            }

            state.voiceHint?.let { hint ->
                LaunchedEffect(hint) {
                    delay(3000)
                    viewModel.clearHint()
                }
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = ExerciseColors.AccentGreen.copy(alpha = 0.2f)
                    ),
                    border = BorderStroke(1.dp, ExerciseColors.AccentGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.VolumeUp,
                            null,
                            tint = ExerciseColors.AccentGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(hint, color = ExerciseColors.TextPrimary, fontSize = 13.sp)
                    }
                }
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
        color = ExerciseColors.CardBackground.copy(alpha = 0.7f),
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
                                        ExerciseColors.AccentGreen.copy(alpha = 0.25f),
                                        ExerciseColors.AccentGreenDark.copy(alpha = 0.15f)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, Float.POSITIVE_INFINITY)
                                )
                                isCompleted -> SolidColor(ExerciseColors.AccentGreenDark.copy(alpha = 0.15f))
                                else -> SolidColor(ExerciseColors.CardBackground)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isSelected -> ExerciseColors.AccentGreen
                                isCompleted -> ExerciseColors.AccentGreenDark.copy(alpha = 0.5f)
                                else -> ExerciseColors.CardBorder
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
                                tint = ExerciseColors.AccentGreen,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            exercise.name,
                            color = when {
                                isSelected -> ExerciseColors.AccentGreen
                                isCompleted -> ExerciseColors.TextSecondary
                                else -> ExerciseColors.TextDim
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
                    stringResource(R.string.completedSets),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ExerciseColors.AccentGreen,
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
        colors = CardDefaults.cardColors(containerColor = ExerciseColors.CardBackground),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    ExerciseColors.AccentGreen.copy(alpha = 0.3f),
                    ExerciseColors.CardBorder
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
                        color = ExerciseColors.TextPrimary,
                        fontSize = 24.sp
                    )
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (exercise != null) {
                        Text(
                            stringResource(R.string.sets) + ": $completedSets / ${exercise.defaultSets}",
                            color = if (completedSets >= exercise.defaultSets)
                                ExerciseColors.AccentGreen
                            else ExerciseColors.TextDim,
                            fontSize = 13.sp
                        )
                        Text(
                            stringResource(R.string.reps) + ": ${exercise.defaultReps}",
                            color = ExerciseColors.TextDim,
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
        colors = CardDefaults.cardColors(containerColor = ExerciseColors.CardBackground),
        border = BorderStroke(1.dp, ExerciseColors.CardBorder)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.repsCaps),
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ExerciseColors.TextDim,
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
                        .background(ExerciseColors.AccentGreen.copy(alpha = 0.1f))
                        .border(1.dp, ExerciseColors.AccentGreen.copy(alpha = 0.3f), CircleShape)
                        .clickable { onDecrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Remove,
                        null,
                        tint = ExerciseColors.AccentGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = "$reps",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = ExerciseColors.AccentGreen,
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
                                    ExerciseColors.AccentGreen,
                                    ExerciseColors.AccentGreenDark
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
                        tint = ExerciseColors.DarkBackground,
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
        colors = CardDefaults.cardColors(containerColor = ExerciseColors.CardBackground),
        border = BorderStroke(1.dp, ExerciseColors.CardBorder)
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
                    .background(ExerciseColors.AccentGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    null,
                    tint = ExerciseColors.AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                stringResource(R.string.weight) + " (кг):",
                color = ExerciseColors.TextSecondary,
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
                    color = ExerciseColors.TextPrimary,
                    textAlign = TextAlign.Center
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ExerciseColors.AccentGreen,
                    unfocusedBorderColor = ExerciseColors.CardBorder,
                    cursorColor = ExerciseColors.AccentGreen
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
                                ExerciseColors.AccentGreen,
                                ExerciseColors.AccentGreenDark
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    } else {
                        SolidColor(ExerciseColors.CardBorder)
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
                    tint = if (enabled) ExerciseColors.DarkBackground
                    else ExerciseColors.TextDim
                )
                Text(
                    stringResource(R.string.completeSets),
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) ExerciseColors.DarkBackground
                    else ExerciseColors.TextDim,
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
            containerColor = ExerciseColors.AccentGreenDark.copy(alpha = 0.08f)
        ),
        border = BorderStroke(1.dp, ExerciseColors.AccentGreen.copy(alpha = 0.1f))
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
                    tint = ExerciseColors.AccentGreen,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    stringResource(R.string.set) + " ${set.setNumber}",
                    color = ExerciseColors.AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "${set.repsDone ?: 0} повт",
                    color = ExerciseColors.TextPrimary,
                    fontSize = 14.sp
                )
                if (set.weightDone != null) {
                    Text(
                        "${set.weightDone} кг",
                        color = ExerciseColors.TextSecondary,
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
            containerColor = ExerciseColors.AccentOrange.copy(alpha = 0.15f),
            disabledContainerColor = ExerciseColors.CardBorder
        )
    ) {
        if (isSubmitting) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = ExerciseColors.AccentOrange
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Flag,
                    null,
                    tint = ExerciseColors.AccentOrange
                )
                Text(
                    stringResource(R.string.completeWorkout),
                    color = ExerciseColors.AccentOrange,
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
            CircularProgressIndicator(color = ExerciseColors.AccentGreen)
            Text(
                stringResource(R.string.loadWorkout),
                color = ExerciseColors.TextSecondary
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
                tint = ExerciseColors.ErrorRed,
                modifier = Modifier.size(48.dp)
            )
            Text(
                message,
                color = ExerciseColors.TextSecondary,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExerciseColors.AccentGreenDark
                )
            ) {
                Text(stringResource(R.string.repeat))
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
                tint = ExerciseColors.AccentGreen,
                modifier = Modifier.size(80.dp)
            )
            Text(
                stringResource(R.string.completedWorkout),
                color = ExerciseColors.TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            Text(
                stringResource(R.string.goodWork),
                color = ExerciseColors.TextSecondary,
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
                            ExerciseColors.AccentGreen,
                            ExerciseColors.AccentGreenDark
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
                    tint = ExerciseColors.DarkBackground
                )
                Text(
                    stringResource(R.string.completeRep),
                    fontWeight = FontWeight.Bold,
                    color = ExerciseColors.DarkBackground,
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
            containerColor = ExerciseColors.AccentGreenDark.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
            1.dp,
            ExerciseColors.AccentGreen.copy(alpha = 0.3f)
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
                tint = ExerciseColors.AccentGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                stringResource(R.string.repSuccess),
                color = ExerciseColors.AccentGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}