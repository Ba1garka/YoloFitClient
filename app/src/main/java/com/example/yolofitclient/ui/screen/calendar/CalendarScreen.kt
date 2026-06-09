package com.example.yolofitclient.ui.screen.calendar

import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.domain.entity.WorkoutEntity
import com.example.yolofitclient.ui.screen.exercise.ExerciseColors
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import androidx.compose.ui.res.stringResource
import com.example.yolofitclient.R

object CalendarColors {
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
    val AccentBlue = Color(0xFF3B82F6)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onWorkoutClick: (Int) -> Unit = {},
    viewModel: CalendarViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
       viewModel.getData()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(CalendarColors.DarkBackground),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CalendarColors.AccentGreen.copy(alpha = 0.14f), Color.Transparent),
                    center = Offset(size.width * 0.7f, size.height * 0.15f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width * 0.7f, size.height * 0.15f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CalendarColors.AccentOrange.copy(alpha = 0.10f), Color.Transparent),
                    center = Offset(size.width * 0.3f, size.height * 0.8f),
                    radius = size.minDimension * 0.5f
                ),
                radius = size.minDimension * 0.5f,
                center = Offset(size.width * 0.3f, size.height * 0.8f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            ) {

            Row {
                Text(
                    text = stringResource(R.string.calendar_title),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 25.dp),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = ExerciseColors.TextPrimary,
                        letterSpacing = 2.sp
                    )
                )
            }


            when (val currentState = state) {
                is CalendarState.Loading -> CalendarLoadingState()
                is CalendarState.Error -> CalendarErrorState(currentState, onRefresh = { viewModel.getData() })
                is CalendarState.Content -> CalendarContentState(
                    workouts = currentState.workouts,
                    onWorkoutClick = onWorkoutClick
                )
            }
        }
    }
}

@Composable
private fun CalendarLoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = CalendarColors.AccentGreen)
    }
}

@Composable
private fun CalendarErrorState(state: CalendarState.Error, onRefresh: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(state.reason, color = CalendarColors.TextSecondary)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRefresh, colors = ButtonDefaults.buttonColors(containerColor = CalendarColors.AccentGreenDark)) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun CalendarContentState(
    workouts: List<WorkoutEntity>,
    onWorkoutClick: (Int) -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val yearMonth = YearMonth.from(selectedDate)

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { selectedDate = selectedDate.minusMonths(1) },
                modifier = Modifier.clip(CircleShape).background(CalendarColors.CardBackground)
            ) {
                Icon(Icons.Default.ArrowBack, stringResource(R.string.back), tint = CalendarColors.AccentGreen)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy")).replaceFirstChar { it.uppercase() },
                    color = CalendarColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(Modifier.height(4.dp))
                TextButton(onClick = { selectedDate = LocalDate.now() }) {
                    Text(
                        stringResource(R.string.today),
                        color = CalendarColors.AccentGreen,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }

            IconButton(
                onClick = { selectedDate = selectedDate.plusMonths(1) },
                modifier = Modifier.clip(CircleShape).background(CalendarColors.CardBackground)
            ) {
                Icon(Icons.Default.ArrowForward, stringResource(R.string.next), tint = CalendarColors.AccentGreen)
            }
        }

        // Дни недели
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
            DayOfWeek.entries.forEach { day ->
                Text(
                    text = day.name.take(2),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    color = CalendarColors.AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        val firstDayOfMonth = yearMonth.atDay(1)
        val firstDayOfCalendar = firstDayOfMonth.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val days = (0 until 42).map { firstDayOfCalendar.plusDays(it.toLong()) }

        val workoutDates = workouts.map { it.workoutDate }.toSet()

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
            days.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        val isCurrentMonth = date.month == yearMonth.month
                        val isToday = date == LocalDate.now()
                        val isSelected = date == selectedDate
                        val hasWorkout = workoutDates.contains(date.toString())

                        CalendarDay(
                            date = date,
                            isCurrentMonth = isCurrentMonth,
                            isToday = isToday,
                            isSelected = isSelected,
                            hasWorkout = hasWorkout,
                            onClick = { selectedDate = date },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(2.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            CalendarColors.AccentGreen.copy(alpha = 0.6f),
                            CalendarColors.AccentOrange.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )

        Spacer(Modifier.height(8.dp))

        val selectedDateWorkouts = workouts.filter { it.workoutDate == selectedDate.toString() }

        Text(
            stringResource(R.string.calendar_subtitle) + " ${selectedDate.format(DateTimeFormatter.ofPattern("dd.MM"))}",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                color = CalendarColors.AccentGreen,
                fontSize = 12.sp
            )
        )

        if (selectedDateWorkouts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        null,
                        tint = CalendarColors.TextDim,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(
                        stringResource(R.string.noWorkoutToday),
                        color = CalendarColors.TextDim,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selectedDateWorkouts) { workout ->
                    CalendarWorkoutCard(
                        workout = workout,
                        onClick = { onWorkoutClick(workout.id) }
                    )
                }
                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun CalendarDay(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    isSelected: Boolean,
    hasWorkout: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> CalendarColors.AccentGreen.copy(alpha = 0.25f)
                    isToday -> CalendarColors.AccentGreen.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            .border(
                width = if (isToday) 2.dp else 0.dp,
                color = if (isToday) CalendarColors.AccentGreen else Color.Transparent,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = when {
                    !isCurrentMonth -> CalendarColors.TextDim.copy(alpha = 0.3f)
                    isSelected -> CalendarColors.AccentGreen
                    isToday -> CalendarColors.AccentGreen
                    else -> CalendarColors.TextPrimary
                },
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )

            if (hasWorkout && isCurrentMonth) {
                Spacer(Modifier.height(2.dp))
                Box(
                    modifier = Modifier.size(5.dp).clip(CircleShape).background(CalendarColors.AccentOrange)
                )
            }
        }
    }
}

@Composable
private fun CalendarWorkoutCard(
    workout: WorkoutEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = DiagonalRoundedCornerShape(
            topLeft = 30f, topRight = 12f, bottomRight = 30f, bottomLeft = 12f
        ),
        colors = CardDefaults.cardColors(containerColor = CalendarColors.CardBackground),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    CalendarColors.AccentGreen.copy(alpha = 0.3f),
                    CalendarColors.CardBorder
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (workout.completed) CalendarColors.AccentGreen.copy(alpha = 0.15f)
                        else CalendarColors.AccentOrange.copy(alpha = 0.15f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (workout.completed) Icons.Default.CheckCircle else Icons.Default.LocalFireDepartment,
                    null,
                    tint = if (workout.completed) CalendarColors.AccentGreen else CalendarColors.AccentOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    workout.startTime,
                    color = CalendarColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    "${workout.exercises.size} упражнений",
                    color = CalendarColors.TextPrimary.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    if (workout.completed) stringResource(R.string.complete) else stringResource(R.string.active),
                    color = if (workout.completed) CalendarColors.AccentGreen else CalendarColors.AccentOrange,
                    fontSize = 13.sp
                )
            }

            Icon(
                Icons.Default.ArrowForward,
                null,
                tint = CalendarColors.TextDim,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}