package com.example.yolofitclient.presentation.ui.screen.home


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.domain.entity.WorkoutEntity
import java.text.SimpleDateFormat
import java.util.*
import com.example.yolofitclient.R
import com.example.yolofitclient.presentation.ui.theme.DiagonalRoundedCornerShape
import com.example.yolofitclient.presentation.ui.theme.ExerciseColors
import com.example.yolofitclient.presentation.viewmodel.HomeViewModel



@Composable
fun HomeScreen(
    onWorkoutStartClick: (Int) -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    onWorkoutDetailClick: (Int) -> Unit,
) {
    val state by homeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        homeViewModel.loadTodayWorkouts()
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
                        ExerciseColors.AccentBlue.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.3f, size.height * 0.8f),
                    radius = size.minDimension * 0.5f
                ),
                radius = size.minDimension * 0.5f,
                center = Offset(size.width * 0.3f, size.height * 0.8f)
            )
        }

        when (val currentState = state) {
            is HomeState.Loading -> LoadingState()
            is HomeState.Error -> ErrorState(currentState)
            is HomeState.Content -> ContentState(
                state = currentState,
                workouts = currentState.todayWorkouts,
                onWorkoutStartClick = onWorkoutStartClick,
                onWorkoutDetailClick = onWorkoutDetailClick,
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = ExerciseColors.AccentGreen
        )
    }
}

@Composable
private fun ErrorState(state: HomeState.Error) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFFF4444),
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = state.reason,
                color = ExerciseColors.TextSecondary,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ContentState(
    state: HomeState.Content,
    workouts: List<WorkoutEntity>,
    onWorkoutStartClick: (Int) -> Unit,
    onWorkoutDetailClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GreetingHeader()
        }

        item {
            CaloriesProgressCard(
                currentCalories = state.dailyCalories,
                targetCalories = state.dailyCalorieTarget
            )
        }

        item {
            Text(
                text = stringResource(R.string.todayWorkouts),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = ExerciseColors.AccentGreen,
                    letterSpacing = 2.sp
                )
            )
        }

        if (workouts.isEmpty()) {
            item {
                NoWorkoutsCard()
            }
        } else {
            items(workouts, key = { it.id }) { workout ->
                WorkoutCard(
                    workout = workout,
                    onStartClick = { onWorkoutStartClick(workout.id) },
                    onWorkoutDetailClick = onWorkoutDetailClick
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun GreetingHeader() {
    val today = SimpleDateFormat("d MMMM, EEEE", Locale("ru")).format(Date())

        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.hello),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = ExerciseColors.TextPrimary,
                        fontSize = 28.sp
                    )
                )

                Text(
                    text = stringResource(R.string.goToWorkout),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = ExerciseColors.TextSecondary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = ExerciseColors.AccentGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = today.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ExerciseColors.AccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }

}

@Composable
fun NoWorkoutsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ExerciseColors.CardBackground
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ExerciseColors.AccentGreen.copy(alpha = 0.2f),
                                ExerciseColors.AccentGreenDark.copy(alpha = 0.1f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = ExerciseColors.AccentGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = stringResource(R.string.noWorkoutToday),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExerciseColors.TextPrimary,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = stringResource(R.string.letsCreateWorkout),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = ExerciseColors.TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            )
        }
    }
}

@Composable
private fun WorkoutCard(
    workout: WorkoutEntity,
    onStartClick: () -> Unit,
    onWorkoutDetailClick: (Int) -> Unit
) {
    val cardShape = DiagonalRoundedCornerShape(
        topLeft = 40f,
        topRight = 16f,
        bottomRight = 40f,
        bottomLeft = 16f
    )

    Card(
        modifier = Modifier.fillMaxWidth().clickable( onClick = { onWorkoutDetailClick(workout.id) }),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = ExerciseColors.CardBackground
        ),
        border = BorderStroke(1.dp,
            Brush.linearGradient(
                colors = listOf(
                    ExerciseColors.AccentGreen.copy(alpha = 0.3f),
                    ExerciseColors.CardBorder,
                    ExerciseColors.AccentBlue.copy(alpha = 0.2f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Box {
            Box(
                modifier = Modifier.matchParentSize().clip(cardShape)
                    .background(
                        Brush.horizontalGradient(
                            0f to ExerciseColors.AccentGreen.copy(alpha = 0.05f),
                            0.5f to ExerciseColors.AccentBlue.copy(alpha = 0.03f),
                            1f to Color.Transparent
                        )
                    )
            )

            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = workout.startTime + " ",
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (workout.completed) ExerciseColors.AccentGreen
                            else ExerciseColors.AccentOrange,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )

                    Text(
                        text = if (workout.completed) stringResource(R.string.complete) else stringResource(R.string.active),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (workout.completed) ExerciseColors.AccentGreen
                            else ExerciseColors.AccentOrange,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${workout.exercises.size} " + stringResource(R.string.exercisesSmall),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ExerciseColors.TextDim,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                workout.exercises.take(3).forEach { exercise ->
                    Text(
                        text = "- ${exercise.name}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ExerciseColors.TextPrimary,
                            fontSize = 11.sp
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                if (workout.exercises.size > 3) {
                    Text(
                        text = "... и ещё ${workout.exercises.size - 3}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ExerciseColors.TextDim
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    ExerciseColors.AccentGreen.copy(alpha = 0.6f),
                                    ExerciseColors.AccentBlue.copy(alpha = 0.3f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (!workout.completed) {
                    Button(
                        onClick = onStartClick,
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            ExerciseColors.AccentGreen,
                                            ExerciseColors.AccentGreenDark
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(Float.POSITIVE_INFINITY, 0f)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = ExerciseColors.DarkBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = stringResource(R.string.startWorkout),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = ExerciseColors.DarkBackground
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun CaloriesProgressCard(
    currentCalories: Int,
    targetCalories: Int?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = ExerciseColors.CardBackground
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    ExerciseColors.AccentOrange.copy(alpha = 0.3f),
                    ExerciseColors.AccentBlue.copy(alpha = 0.2f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = ExerciseColors.AccentOrange,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(R.string.calorii),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = ExerciseColors.TextPrimary,
                        letterSpacing = 1.sp
                    )
                )
            }

            if (targetCalories != null && targetCalories > 0) {
                val progress = (currentCalories.toFloat() / targetCalories).coerceIn(0f, 1f)
                val percentage = (progress * 100).toInt()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ExerciseColors.CardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        ExerciseColors.AccentOrange,
                                        Color(0xFFFF6B35),
                                        ExerciseColors.AccentGreen
                                    ),
                                    startX = 0f,
                                    endX = Float.POSITIVE_INFINITY,
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$currentCalories " + stringResource(R.string.kkal),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ExerciseColors.AccentOrange,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ExerciseColors.AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$targetCalories " + stringResource(R.string.kkal),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = ExerciseColors.TextSecondary
                        )
                    )
                }
            } else {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$currentCalories " + stringResource(R.string.kkal),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExerciseColors.AccentOrange,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = stringResource(R.string.noTarget),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ExerciseColors.TextDim
                        )
                    )
                }
            }
        }
    }
}