package com.example.yolofitclient.ui.screen.workoutdetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.domain.entity.ExerciseSetDetailEntity
import com.example.yolofitclient.domain.entity.WorkoutDetailEntity
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape
import com.example.yolofitclient.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Int,
    onBack: () -> Unit,
    viewModel: WorkoutDetailViewModel = viewModel { WorkoutDetailViewModel(workoutId) }
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize().background(WorkoutDetailColors.DarkBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(WorkoutDetailColors.AccentGreen.copy(alpha = 0.08f), Color.Transparent),
                    center = Offset(size.width * 0.7f, size.height * 0.1f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width * 0.7f, size.height * 0.1f)
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text(stringResource(R.string.detailWorkout), color = WorkoutDetailColors.TextPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = WorkoutDetailColors.AccentGreen)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WorkoutDetailColors.CardBackground
                )
            )

            when (val currentState = state) {
                is WorkoutDetailState.Loading -> LoadingContent()
                is WorkoutDetailState.Error -> ErrorContent(currentState.reason)
                is WorkoutDetailState.Content -> DetailContent(currentState.detail)
            }
        }
    }
}

@Composable
private fun DetailContent(detail: WorkoutDetailEntity) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { WorkoutHeaderCard(detail) }

        item { CaloriesCard(detail.totalCalories) }

        item {
            Text(
                stringResource(R.string.completeSets),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = WorkoutDetailColors.AccentGreen,
                    letterSpacing = 2.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        val groupedSets = detail.exerciseSets.groupBy { it.exerciseName }

        groupedSets.forEach { (exerciseName, sets) ->
            item { ExerciseGroupCard(exerciseName = exerciseName, sets = sets) }
        }

        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun WorkoutHeaderCard(detail: WorkoutDetailEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = DiagonalRoundedCornerShape(
            topLeft = 40f, topRight = 16f, bottomRight = 40f, bottomLeft = 16f
        ),
        colors = CardDefaults.cardColors(containerColor = WorkoutDetailColors.CardBackground),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    WorkoutDetailColors.AccentGreen.copy(alpha = 0.3f),
                    WorkoutDetailColors.CardBorder
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    detail.workoutDate,
                    color = WorkoutDetailColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Text(
                    detail.startTime,
                    color = WorkoutDetailColors.TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (detail.completed) WorkoutDetailColors.AccentGreen.copy(alpha = 0.15f)
                    else WorkoutDetailColors.AccentOrange.copy(alpha = 0.15f)
                ) {
                    Text(
                        if (detail.completed) stringResource(R.string.complete) else stringResource(R.string.active),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = if (detail.completed) WorkoutDetailColors.AccentGreen
                        else WorkoutDetailColors.AccentOrange,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                StatChip(
                    label = stringResource(R.string.listExercise),
                    value = "${detail.exerciseSets.groupBy { it.exerciseName }.size}"
                )
                StatChip(
                    label = stringResource(R.string.sets),
                    value = "${detail.exerciseSets.size}"
                )
            }
        }
    }
}

@Composable
private fun CaloriesCard(totalCalories: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WorkoutDetailColors.CardBackground),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    WorkoutDetailColors.AccentOrange.copy(alpha = 0.3f),
                    WorkoutDetailColors.AccentGreen.copy(alpha = 0.2f)
                ),
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            )
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.LocalFireDepartment,
                null,
                tint = WorkoutDetailColors.AccentOrange,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    stringResource(R.string.burnedKKal),
                    color = WorkoutDetailColors.TextDim,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    "$totalCalories " + stringResource(R.string.kkal),
                    color = WorkoutDetailColors.AccentOrange,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }
    }
}

@Composable
private fun ExerciseGroupCard(exerciseName: String, sets: List<ExerciseSetDetailEntity>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WorkoutDetailColors.CardBackground),
        border = BorderStroke(1.dp, WorkoutDetailColors.CardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    null,
                    tint = WorkoutDetailColors.AccentGreen,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    exerciseName,
                    color = WorkoutDetailColors.AccentGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
                    .background(WorkoutDetailColors.CardBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text("№", color = WorkoutDetailColors.TextDim, fontSize = 12.sp, modifier = Modifier.width(30.dp))
                Text("Повт", color = WorkoutDetailColors.TextDim, fontSize = 12.sp, modifier = Modifier.width(50.dp))
                Text(stringResource(R.string.weight)+"(кг)", color = WorkoutDetailColors.TextDim, fontSize = 12.sp, modifier = Modifier.width(70.dp))
                Text(stringResource(R.string.Kkal), color = WorkoutDetailColors.TextDim, fontSize = 12.sp, modifier = Modifier.width(50.dp))
                Text(stringResource(R.string.mistakes), color = WorkoutDetailColors.TextDim, fontSize = 12.sp)
            }

            sets.forEach { set ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        "${set.setNumber}",
                        color = WorkoutDetailColors.AccentGreen,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(30.dp)
                    )
                    Text("${set.repsDone ?: 0}", color = WorkoutDetailColors.TextPrimary, modifier = Modifier.width(50.dp))
                    Text(
                        set.weightDone?.let { "${it}" } ?: "—",
                        color = WorkoutDetailColors.TextSecondary,
                        modifier = Modifier.width(70.dp)
                    )
                    Text(
                        "${set.caloriesBurned}",
                        color = WorkoutDetailColors.AccentOrange,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.width(50.dp)
                    )
                    Text(
                        if (set.mistakeCount > 0) "${set.mistakeCount}" else stringResource(R.string.ok),
                        color = if (set.mistakeCount > 0) Color(0xFFFF4444) else WorkoutDetailColors.AccentGreen,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip( label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {

            Text(value, color = WorkoutDetailColors.TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, color = WorkoutDetailColors.TextDim, fontSize = 10.sp)

    }
}

@Composable
private fun LoadingContent() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = WorkoutDetailColors.AccentGreen)
    }
}

@Composable
private fun ErrorContent(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message, color = WorkoutDetailColors.TextSecondary)
    }
}

object WorkoutDetailColors {
    val DarkBackground = Color(0xFF0D0E0D)
    val CardBackground = Color(0xFF1A1F1A)
    val CardBorder = Color(0xFF2A3A2A)
    val AccentGreen = Color(0xFFB2EA1B)
    val AccentGreenDark = Color(0xFF2AC274)
    val TextPrimary = Color(0xFFF0F0F0)
    val TextSecondary = Color(0xFFB0B0B0)
    val TextDim = Color(0xFF808080)
    val AccentOrange = Color(0xFFFF6D00)
}