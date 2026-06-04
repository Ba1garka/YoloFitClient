package com.example.yolofitclient.ui.screen.home


import android.widget.Toast
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.domain.entity.WorkoutEntity
import com.example.yolofitclient.ui.screen.profile.ProfileState
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape
import java.text.SimpleDateFormat
import java.util.*

object HomeColors {
    val DarkBackground = Color(0xFF0D0E0D)
    val CardBackground = Color(0xFF1A1F1A)
    val CardBorder = Color(0xFF2A3A2A)
    val AccentGreen = Color(0xFFB2EA1B)
    val AccentGreenDark = Color(0xFF2AC274)
    val AccentGreenLight = Color(0xFFC4FF66)
    val TextPrimary = Color(0xFFF0F0F0)
    val TextSecondary = Color(0xFFB0B0B0)
    val TextDim = Color(0xFF808080)
    val AccentBlue = Color(0xFF3B82F6)
    val AccentOrange = Color(0xFFFF6D00)
}

@Composable
fun HomeScreen(
    onWorkoutStartClick: (Int) -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    onWorkoutDetailClick: (Int) -> Unit,
) {
    val state by homeViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadTodayWorkouts()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(HomeColors.DarkBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        HomeColors.AccentGreen.copy(alpha = 0.1f),
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
                        HomeColors.AccentBlue.copy(alpha = 0.05f),
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
            color = HomeColors.AccentGreen
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
                color = HomeColors.TextSecondary,
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
                text = "ТРЕНИРОВКИ НА СЕГОДНЯ",
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = HomeColors.AccentGreen,
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = DiagonalRoundedCornerShape(
            topLeft = 60f,
            topRight = 20f,
            bottomRight = 60f,
            bottomLeft = 20f
        ),
        colors = CardDefaults.cardColors(
            containerColor = HomeColors.CardBackground
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    HomeColors.AccentGreen.copy(alpha = 0.3f),
                    HomeColors.AccentBlue.copy(alpha = 0.2f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(24.dp)
        ) {

            Box(
                modifier = Modifier.matchParentSize()
                    .clip(DiagonalRoundedCornerShape(
                        topLeft = 60f,
                        topRight = 20f,
                        bottomRight = 60f,
                        bottomLeft = 20f
                    ))
                    .background(
                        Brush.horizontalGradient(
                            0f to HomeColors.AccentGreen.copy(alpha = 0.08f),
                            0.5f to HomeColors.AccentBlue.copy(alpha = 0.05f),
                            1f to Color.Transparent
                        )
                    )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Привет!",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HomeColors.TextPrimary,
                        fontSize = 28.sp
                    )
                )

                Text(
                    text = "Готов к тренировке?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = HomeColors.TextSecondary
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
                        tint = HomeColors.AccentGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = today.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = HomeColors.AccentGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
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
            containerColor = HomeColors.CardBackground
        ),
        border = BorderStroke(
            1.dp,
            HomeColors.CardBorder
        )
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
                                HomeColors.AccentGreen.copy(alpha = 0.2f),
                                HomeColors.AccentGreenDark.copy(alpha = 0.1f)
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
                    tint = HomeColors.AccentGreen,
                    modifier = Modifier.size(36.dp)
                )
            }

            Text(
                text = "Нет тренировок",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.TextPrimary,
                    textAlign = TextAlign.Center
                )
            )

            Text(
                text = "Создайте новую тренировку\nв разделе \"База\"",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = HomeColors.TextSecondary,
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
            containerColor = HomeColors.CardBackground
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    HomeColors.AccentGreen.copy(alpha = 0.3f),
                    HomeColors.CardBorder,
                    HomeColors.AccentBlue.copy(alpha = 0.2f)
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
                            0f to HomeColors.AccentGreen.copy(alpha = 0.05f),
                            0.5f to HomeColors.AccentBlue.copy(alpha = 0.03f),
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
                        text = workout.startTime,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = if (workout.completed) HomeColors.AccentGreen
                            else HomeColors.AccentOrange,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    )

                    Text(
                        text = if (workout.completed) "Завершена" else "Активна",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (workout.completed) HomeColors.AccentGreen
                            else HomeColors.AccentOrange,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    )

                    Text(
                        text = "${workout.exercises.size} упражнений",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = HomeColors.TextDim,
                            letterSpacing = 1.sp
                        )
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Упражнения:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = HomeColors.TextDim,
                        fontSize = 11.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                workout.exercises.take(3).forEach { exercise ->
                    Text(
                        text = "• ${exercise.name}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = HomeColors.TextPrimary,
                            fontSize = 14.sp
                        ),
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                if (workout.exercises.size > 3) {
                    Text(
                        text = "... и ещё ${workout.exercises.size - 3}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = HomeColors.TextDim
                        )
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(2.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    HomeColors.AccentGreen.copy(alpha = 0.6f),
                                    HomeColors.AccentBlue.copy(alpha = 0.3f),
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
                                            HomeColors.AccentGreen,
                                            HomeColors.AccentGreenDark
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
                                    tint = HomeColors.DarkBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "Начать тренировку",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = HomeColors.DarkBackground
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
            containerColor = HomeColors.CardBackground
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    HomeColors.AccentOrange.copy(alpha = 0.3f),
                    HomeColors.AccentBlue.copy(alpha = 0.2f)
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
                    tint = HomeColors.AccentOrange,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "КАЛОРИИ",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = HomeColors.TextPrimary,
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
                        .background(HomeColors.CardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        HomeColors.AccentOrange,
                                        Color(0xFFFF6B35),
                                        HomeColors.AccentGreen
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
                        text = "$currentCalories ккал",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = HomeColors.AccentOrange,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = HomeColors.AccentGreen,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "$targetCalories ккал",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = HomeColors.TextSecondary
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
                        text = "$currentCalories ккал",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = HomeColors.AccentOrange,
                            fontSize = 28.sp
                        )
                    )
                    Text(
                        text = "Цель не задана",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = HomeColors.TextDim
                        )
                    )
                }
            }
        }
    }
}