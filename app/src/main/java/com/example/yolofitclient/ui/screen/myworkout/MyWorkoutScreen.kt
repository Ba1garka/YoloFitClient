package com.example.yolofitclient.ui.screen.myworkout

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.domain.entity.WorkoutEntity
import com.example.yolofitclient.ui.screen.exercise.ExerciseColors
import com.example.yolofitclient.ui.screen.home.NoWorkoutsCard
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape
import com.example.yolofitclient.R


@Composable
fun MyWorkoutScreen(
    onWorkoutClick : (Int) -> Unit,
    viewModel: MyWorkoutViewModel = viewModel<MyWorkoutViewModel>()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val selectedIds = remember { mutableStateListOf<Int>() }

    LaunchedEffect(Unit) {
        viewModel.getWorkouts()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(ExerciseColors.DarkBackground)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.radialGradient(
                colors = listOf(
                    ExerciseColors.AccentGreen.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.8f, size.height * 0.2f),
                radius = size.minDimension * 0.7f
            )
            drawRect(gradient)

            val gradient2 = Brush.radialGradient(
                colors = listOf(
                    ExerciseColors.AccentGreenDark.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.2f, size.height * 0.8f),
                radius = size.minDimension * 0.5f
            )
            drawRect(gradient2)
        }

        when(val currentState = state){
            is MyWorkoutState.Error -> ListErrorState(currentState, onRefresh = {viewModel.getWorkouts()} )
            is MyWorkoutState.Loading -> ListLoadingState()
            is MyWorkoutState.Content -> ListContentState(
                currentState,
                selectedIds ,
                onWorkoutDeleteClick = {
                    viewModel.delete(selectedIds)
                    selectedIds.clear()
                },
                onWorkoutClick
            )
            is MyWorkoutState.Success -> {
                Toast.makeText(context, stringResource(R.string.deleteSuccessful), Toast.LENGTH_LONG).show()
            }
        }
    }
}

@Composable
private fun ListLoadingState(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = ExerciseColors.AccentGreen
        )
    }
}

@Composable
private fun ListErrorState(
    state: MyWorkoutState.Error,
    onRefresh: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){
            Text(
                text = state.reason,
                color = ExerciseColors.TextSecondary,
                fontSize = 16.sp
            )
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExerciseColors.AccentGreenDark
                )
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun ListContentState(
    state: MyWorkoutState.Content,
    selectedIds: SnapshotStateList<Int>,
    onWorkoutDeleteClick: (List<Int>) -> Unit,
    onWorkoutClick : (Int) -> Unit
){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        item {
            Text(
                text = stringResource(R.string.calendar_subtitle),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = ExerciseColors.TextPrimary,
                    letterSpacing = 2.sp
                )
            )

            if (selectedIds.isNotEmpty()) {
                Button(
                    onClick = { onWorkoutDeleteClick(selectedIds.toList()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExerciseColors.AccentGreenDark
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.delete) + " (${selectedIds.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (state.workouts.isEmpty()) {
            item {
                NoWorkoutsCard()
            }
        } else {
            items(state.workouts) { workout ->
                WorkoutCard(
                    workout = workout,
                    isSelected = selectedIds.contains(workout.id),
                    onSelectionToggle = {
                        if (selectedIds.contains(workout.id)) {
                            selectedIds.remove(workout.id)
                        } else {
                            selectedIds.add(workout.id)
                        }
                    },
                    onClick = {
                        onWorkoutClick(workout.id)
                    }
                )
            }
        }

    }
}

@Composable
fun WorkoutCard(
    workout: WorkoutEntity,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onSelectionToggle: () -> Unit = {},
    onClick: () -> Unit
) {
    val cardShape = DiagonalRoundedCornerShape(
        topLeft = 90f,
        topRight = 20f,
        bottomRight = 90f,
        bottomLeft = 20f
    )

    Card(
        modifier = modifier.fillMaxWidth()
            .then(
                if (isSelected) Modifier.border(
                    2.dp,
                    Brush.linearGradient(
                        colors = listOf(
                            ExerciseColors.AccentGreen,
                            ExerciseColors.AccentGreenDark
                        )
                    ),
                    cardShape
                ) else Modifier
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = ExerciseColors.CardBackground
        ),
        border = if (!isSelected) BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    ExerciseColors.AccentGreen.copy(alpha = 0.3f),
                    ExerciseColors.CardBorder,
                    ExerciseColors.AccentGreen.copy(alpha = 0.1f)
                )
            )
        ) else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 12.dp else 8.dp
        ),
        onClick = onClick
    ) {
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            0f to ExerciseColors.AccentGreen.copy(alpha = 0.05f),
                            0.3f to ExerciseColors.AccentGreen.copy(alpha = 0.02f),
                            1f to Color.Transparent
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = workout.workoutDate,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExerciseColors.TextPrimary,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Text(
                        text = workout.startTime,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExerciseColors.TextPrimary,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Text(
                        text = workout.completed.toString() ,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ExerciseColors.AccentGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp
                        )
                    )
                }

                IconButton(
                    onClick = {
                        onSelectionToggle()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected)
                                ExerciseColors.AccentGreen.copy(alpha = 0.2f)
                            else
                                ExerciseColors.CardBorder.copy(alpha = 0.2f)
                        )
                ) {
                    Icon(
                        imageVector = if (isSelected) Icons.Default.CheckCircle
                        else Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = if (isSelected) ExerciseColors.AccentGreen else ExerciseColors.TextDim,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

