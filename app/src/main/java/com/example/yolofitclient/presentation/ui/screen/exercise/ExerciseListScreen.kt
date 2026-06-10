package com.example.yolofitclient.presentation.ui.screen.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.yolofitclient.domain.entity.ExerciseEntity
import com.example.yolofitclient.R
import com.example.yolofitclient.presentation.ui.theme.DiagonalRoundedCornerShape
import com.example.yolofitclient.presentation.ui.theme.ExerciseColors
import com.example.yolofitclient.presentation.viewmodel.ExerciseListViewModel


@Composable
fun ExerciseListScreen(
    exerciseListViewModel: ExerciseListViewModel = viewModel<ExerciseListViewModel>(),
    onWorkoutCreateClick: (List<Int>) -> Unit
) {
    val state by exerciseListViewModel.uiState.collectAsStateWithLifecycle()

    val selectedIds = remember { mutableStateListOf<Int>() }

    Box(
        modifier = Modifier.fillMaxSize().background(ExerciseColors.DarkBackground)
    ) {

        Canvas(modifier = Modifier.fillMaxSize()) {
            val gradient = Brush.radialGradient(
                colors = listOf(
                    ExerciseColors.AccentGreen.copy(alpha = 0.17f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.8f, size.height * 0.2f),
                radius = size.minDimension * 0.7f
            )
            drawRect(gradient)

            val gradient2 = Brush.radialGradient(
                colors = listOf(
                    ExerciseColors.AccentGreenDark.copy(alpha = 0.09f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.2f, size.height * 0.8f),
                radius = size.minDimension * 0.5f
            )
            drawRect(gradient2)
        }

        when(val currentState = state){
            is ExerciseListState.Error -> ListErrorState(currentState, onRefresh = {exerciseListViewModel.getData()} )
            is ExerciseListState.Loading -> ListLoadingState()
            is ExerciseListState.Content -> ListContentState(currentState, onWorkoutCreateClick, selectedIds)
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
    state: ExerciseListState.Error,
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
    state: ExerciseListState.Content,
    onWorkoutCreateClick: (List<Int>) -> Unit,
    selectedIds : SnapshotStateList<Int>,
){
    var searchQuery by remember { mutableStateOf("") }

    val filteredExercises = remember(state.users, searchQuery) {
        if (searchQuery.isBlank()) state.users
        else state.users.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        item {
            Text(
                text = stringResource(R.string.exercises),
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = ExerciseColors.TextPrimary,
                    letterSpacing = 2.sp
                )
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                placeholder = { Text(stringResource(R.string.searchExercises), color = ExerciseColors.TextDim) },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = ExerciseColors.AccentGreen)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, null, tint = ExerciseColors.TextDim)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ExerciseColors.AccentGreen,
                    unfocusedBorderColor = ExerciseColors.CardBorder,
                    cursorColor = ExerciseColors.AccentGreen,
                    focusedTextColor = ExerciseColors.TextPrimary,
                    unfocusedTextColor = ExerciseColors.TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedIds.isNotEmpty()) {
                Button(
                    onClick = { onWorkoutCreateClick(selectedIds.toList()) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExerciseColors.AccentGreenDark
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.create) + " (${selectedIds.size})",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }

        items(filteredExercises) { exercise ->
            ExerciseCard(
                exercise = exercise,
                isSelected = selectedIds.contains(exercise.id),
                onSelectionToggle = {
                    if (selectedIds.contains(exercise.id)) {
                        selectedIds.remove(exercise.id)
                    } else {
                        selectedIds.add(exercise.id)
                    }
                },
                onClick = { }
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: ExerciseEntity,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onSelectionToggle: () -> Unit,
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
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(110.dp).clip(RoundedCornerShape(10.dp))
                ) {
                    AsyncImage(
                        model = "https://i.pinimg.com/originals/3b/b0/28/3bb028b7dcd1bf9d39f08ab3a2102e67.jpg?nii=t", // TODO()
                        contentDescription = exercise.name,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExerciseColors.TextPrimary,
                            fontSize = 18.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = exercise.bodyZoneName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ExerciseColors.AccentGreen,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        ExerciseColors.AccentGreen.copy(alpha = 0.8f),
                                        ExerciseColors.AccentGreen.copy(alpha = 0.2f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ParameterChip(
                            label1 = stringResource(R.string.sets),
                            value1 = "${exercise.defaultSets}",
                            label2 = stringResource(R.string.reps),
                            value2 = "${exercise.defaultReps}"
                        )
                    }
                }

                IconButton(
                    onClick = {
                        onSelectionToggle()
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) ExerciseColors.AccentGreen.copy(alpha = 0.2f)
                            else ExerciseColors.CardBorder.copy(alpha = 0.2f)
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

@Composable
private fun ParameterChip(
    label1: String,
    value1: String,
    label2: String,
    value2: String,
) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(10.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ExerciseColors.AccentGreen.copy(alpha = 0.15f),
                        ExerciseColors.AccentGreen.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(horizontal = 5.dp, vertical = 5.dp)
    ) {
        Column {
            Text(
                text = "$label1: $value1 $label2: $value2",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ExerciseColors.TextDim,
                    fontSize = 10.sp,
                )
            )
        }
    }
}