package com.example.yolofitclient.ui.screen.exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.yolofitclient.domain.entity.ExerciseEntity
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape
import com.example.yolofitclient.ui.theme.YoloFitClientTheme


object ExerciseColors {
    val DarkBackground = Color(0xFF0D0E0D)
    val CardBackground = Color(0xFF1A1F1A)
    val CardBorder = Color(0xFF3A2A2D)
    val AccentGreen = Color(0xFFB2EA1B)
    val AccentGreenDark = Color(0xFF2AC274)
    val AccentGreenLight = Color(0xFFC4FF66)
    val TextPrimary = Color(0xFFF0F0F0)
    val TextSecondary = Color(0xFFB0B0B0)
    val TextDim = Color(0xFF808080)
}

@Composable
fun ExerciseListScreen(
    exerciseListViewModel: ExerciseListViewModel = viewModel<ExerciseListViewModel>()
) {
    val state by exerciseListViewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ExerciseColors.DarkBackground)
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
            is ExerciseListState.Content -> ListContentState(currentState)
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
                Text("Обновить")
            }
        }
    }
}

@Composable
private fun ListContentState(
    state: ExerciseListState.Content
){
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        item {
            Text(
                text = "УПРАЖНЕНИЯ",
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 12.dp),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = ExerciseColors.TextPrimary,
                    letterSpacing = 2.sp
                )
            )
        }

        items(state.users) { exercise ->
            ExerciseCard(exercise = exercise)
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: ExerciseEntity,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val cardShape = DiagonalRoundedCornerShape(
        topLeft = 90f,
        topRight = 20f,
        bottomRight = 90f,
        bottomLeft = 20f
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = ExerciseColors.CardBackground
        ),
        border = BorderStroke(
            1.dp,
            Brush.linearGradient(
                colors = listOf(
                    ExerciseColors.AccentGreen.copy(alpha = 0.3f),
                    ExerciseColors.CardBorder,
                    ExerciseColors.AccentGreen.copy(alpha = 0.1f)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        onClick = onClick
    ) {
        Box {
            // Градиентный оверлей на карточке
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Изображение упражнения с градиентной рамкой
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ExerciseColors.CardBorder)
                        .then(
                            Modifier.background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        ExerciseColors.AccentGreen,
                                        ExerciseColors.AccentGreenDark
                                    )
                                ),
                                RoundedCornerShape(16.dp)
                            )
                        )
                ) {
                    AsyncImage(
                        model = "https://i.pinimg.com/originals/3b/b0/28/3bb028b7dcd1bf9d39f08ab3a2102e67.jpg?nii=t",
                        contentDescription = exercise.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )

                    // Градиентный оверлей на изображении
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        ExerciseColors.AccentGreen.copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )
                }

                // Информация об упражнении
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Название упражнения
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = ExerciseColors.TextPrimary,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    )

                    // Зона тела с градиентным текстом
                    Text(
                        text = exercise.bodyZoneName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = ExerciseColors.AccentGreen,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    )

                    // Градиентный разделитель
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

                    // Параметры упражнения
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Подходы
                        ParameterChip(
                            label = "Подходы",
                            value = "${exercise.defaultSets}"
                        )

                        // Повторения
                        ParameterChip(
                            label = "Повторы",
                            value = "${exercise.defaultReps}"
                        )

                        // Коэффициент если не пустой
                        if (exercise.weightCoefficient.isNotEmpty() && exercise.weightCoefficient != "0") {
                            ParameterChip(
                                label = "Коэф.",
                                value = exercise.weightCoefficient
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParameterChip(
    label: String,
    value: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ExerciseColors.AccentGreen.copy(alpha = 0.15f),
                        ExerciseColors.AccentGreen.copy(alpha = 0.05f)
                    )
                )
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExerciseColors.AccentGreen,
                    fontSize = 20.sp
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = ExerciseColors.TextDim,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YoloFitClientTheme {
        ExerciseListScreen()
    }
}