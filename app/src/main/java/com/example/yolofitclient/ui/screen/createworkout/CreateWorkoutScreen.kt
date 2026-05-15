package com.example.yolofitclient.ui.screen.createworkout


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.ui.screen.exercise.ExerciseColors
import com.example.yolofitclient.ui.theme.AuthColors
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    selectedIds: List<Int>,
    onBackClick: () -> Unit,
    onWorkoutCreated: () -> Unit,
    createWorkoutViewModel: CreateWorkoutViewModel = viewModel<CreateWorkoutViewModel>(),
) {
    val state by createWorkoutViewModel.uiState.collectAsState()

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
            is CreateWorkoutState.Error -> ErrorState(currentState, onRefresh = {} )
            is CreateWorkoutState.Loading -> LoadingState()
            is CreateWorkoutState.Content -> ContentState(selectedIds, onBackClick, onWorkoutCreated, createWorkoutViewModel)
            is CreateWorkoutState.Success -> SuccessState()
        }
    }
}

@Composable
private fun SuccessState() {
    Box(
        modifier = Modifier.fillMaxSize().background(AuthColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = AuthColors.AccentGreen,
                modifier = Modifier.size(80.dp)
            )

            Text(
                text = "Тренировка успешно создана",
                color = AuthColors.TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun LoadingState(){
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
private fun ErrorState(
    state: CreateWorkoutState.Error,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContentState(
    selectedIds: List<Int>,
    onBackClick: () -> Unit,
    onWorkoutCreated: () -> Unit,
    createWorkoutViewModel: CreateWorkoutViewModel
){
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0x000D0E0D),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Новая тренировка",
                        color = Color(0xFFF0F0F0)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Назад",
                            tint = Color(0xFFB2EA1B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1F1A)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1F1A)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF2A3A2A)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Выбрано упражнений",
                        color = Color(0xFF808080),
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${selectedIds.size}",
                        color = Color(0xFFB2EA1B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "ID: ${selectedIds.joinToString(", ")}",
                        color = Color(0xFFB0B0B0),
                        fontSize = 12.sp
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1F1A)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF2A3A2A)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Дата тренировки",
                            color = Color(0xFF808080),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            selectedDate ?: "Не выбрана",
                            color = if (selectedDate != null) Color(0xFFB2EA1B) else Color(0xFF808080),
                            fontSize = 18.sp,
                            fontWeight = if (selectedDate != null) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = Color(0xFFB2EA1B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(Modifier.weight(1f))


            Button(
                onClick = {
                    createWorkoutViewModel.createWorkout(selectedDate?: LocalDate.now().toString(), selectedIds)
                    onWorkoutCreated()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedIds.isNotEmpty() && selectedDate != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2AC274),
                    disabledContainerColor = Color(0xFF2A3A2A)
                )
            ) {
                Text(
                    "Создать тренировку",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (selectedIds.isNotEmpty() && selectedDate != null) Color.White
                    else Color(0xFF808080)
                )
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(millis))
                        selectedDate = date
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = Color(0xFFB2EA1B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена", color = Color(0xFF808080))
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color(0xFF1A1F1A),
                    titleContentColor = Color(0xFFF0F0F0),
                    headlineContentColor = Color(0xFFF0F0F0),
                    weekdayContentColor = Color(0xFFB2EA1B),
                    subheadContentColor = Color(0xFFF0F0F0),
                    navigationContentColor = Color(0xFFB2EA1B),
                    yearContentColor = Color(0xFFB2EA1B),
                    selectedYearContainerColor = Color(0xFF2AC274),
                    dayContentColor = Color(0xFFF0F0F0),
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF2AC274),
                    todayDateBorderColor = Color(0xFFB2EA1B),
                    todayContentColor = Color(0xFFB2EA1B)
                )
            )
        }
    }
}