package com.example.yolofitclient.ui.screen.createworkout


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.ui.screen.exercise.ExerciseColors
import com.example.yolofitclient.ui.theme.AuthColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.yolofitclient.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkoutScreen(
    selectedIds: List<Int>,
    onBackClick: () -> Unit,
    onWorkoutCreated: () -> Unit,
    createWorkoutViewModel: CreateWorkoutViewModel = viewModel<CreateWorkoutViewModel>(),
) {
    val state by createWorkoutViewModel.uiState.collectAsStateWithLifecycle()

    val timeSlotsState by createWorkoutViewModel.timeSlotsState.collectAsStateWithLifecycle()

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
            is CreateWorkoutState.Content -> ContentState(selectedIds, onBackClick, onWorkoutCreated, createWorkoutViewModel, timeSlotsState)
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
                text = stringResource(R.string.workoutCreate),
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
                Text(stringResource(R.string.retry))
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
    createWorkoutViewModel: CreateWorkoutViewModel,
    timeSlotsState: SlotsState
){
    var selectedDate by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    var selectedTime by remember { mutableStateOf<String?>(null) }
    var showTimeSlots by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            createWorkoutViewModel.getTime(date)
        }
    }

    Scaffold(
        containerColor = Color(0x000D0E0D),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.newWorkout), color = Color(0xFFF0F0F0))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
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
                border = BorderStroke(
                    1.dp,
                    Color(0xFF2A3A2A)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        stringResource(R.string.selectedExercise),
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
                border = BorderStroke(
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
                            stringResource(R.string.dataWorkout),
                            color = Color(0xFF808080),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            selectedDate ?: stringResource(R.string.noSelected),
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


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = selectedDate != null) {
                        if (selectedDate != null) {
                            createWorkoutViewModel.getTime(selectedDate!!)
                            showTimeSlots = true
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F1A)),
                border = BorderStroke(1.dp, Color(0xFF2A3A2A))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            stringResource(R.string.timeWorkout),
                            color = Color(0xFF808080),
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            selectedTime ?: if (selectedDate != null) stringResource(R.string.noSelected) else stringResource(R.string.selectData),
                            color = if (selectedTime != null) Color(0xFFB2EA1B)
                            else Color(0xFF808080),
                            fontSize = 18.sp,
                            fontWeight = if (selectedTime != null) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                    Icon(
                        Icons.Default.AccessTime,
                        null,
                        tint = if (selectedDate != null) Color(0xFFB2EA1B) else Color(0xFF808080),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    selectedDate?.let { date ->
                        val dateTime = if (selectedTime != null) "$selectedTime" else "00:00:00"
                        createWorkoutViewModel.createWorkout(
                            date,
                            selectedIds,
                            dateTime
                        )
                        onWorkoutCreated()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = selectedIds.isNotEmpty() && selectedDate != null && selectedTime != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2AC274),
                    disabledContainerColor = Color(0xFF2A3A2A)
                )
            ) {
                Text(
                    stringResource(R.string.createWorkout),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (selectedIds.isNotEmpty() && selectedDate != null) Color.White else Color(0xFF808080)
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
                        selectedTime = null
                        createWorkoutViewModel.getTime(date)
                    }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok), color = Color(0xFFB2EA1B))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel), color = Color(0xFF808080))
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

    if (showTimeSlots) {
        when (val timeState = timeSlotsState) {
            is SlotsState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 300.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(stringResource(R.string.selectData))
                }
            }

            is SlotsState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(top = 300.dp)
                ) {
                    Column {
                        Text(timeState.reason)
                        Button(onClick = {
                            selectedDate?.let { createWorkoutViewModel.getTime(it) }
                        }) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }

            is SlotsState.Content -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showTimeSlots = false
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {  },
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F1A)),
                        border = BorderStroke(
                            1.dp,
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFB2EA1B).copy(alpha = 0.3f),
                                    Color(0xFF2AC274).copy(alpha = 0.2f)
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
                                    stringResource(R.string.selectTime),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFB2EA1B),
                                        letterSpacing = 2.sp
                                    )
                                )
                                IconButton(onClick = { showTimeSlots = false }) {
                                    Icon(
                                        Icons.Default.Close,
                                        null,
                                        tint = Color(0xFF808080)
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text(selectedDate ?: "", color = Color(0xFF808080), fontSize = 13.sp)

                            Spacer(Modifier.height(12.dp))

                            LazyColumn(
                                modifier = Modifier.height(320.dp)
                            ) {
                                items(timeSlotsState.timeSlots.chunked(4)) { rowSlots ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowSlots.forEach { slot ->
                                            val isSelected = selectedTime == slot.startTime

                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(
                                                        if (isSelected) {
                                                            Brush.linearGradient(
                                                                colors = listOf(
                                                                    Color(0xFF2AC274).copy(alpha = 0.3f),
                                                                    Color(0xFFB2EA1B).copy(alpha = 0.15f)
                                                                ),
                                                                start = Offset(0f, 0f),
                                                                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                                            )
                                                        } else {
                                                            SolidColor(Color(0xFF252A25))
                                                        }
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) Color(0xFFB2EA1B)
                                                        else Color(0xFF2A3A2A),
                                                        RoundedCornerShape(12.dp)
                                                    )
                                                    .clickable {
                                                        selectedTime = slot.startTime
                                                        showTimeSlots = false
                                                    }
                                                    .padding(vertical = 12.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    slot.startTime,
                                                    color = if (isSelected) Color(0xFFB2EA1B)
                                                    else Color(0xFFF0F0F0),
                                                    fontWeight = if (isSelected) FontWeight.Bold
                                                    else FontWeight.Normal,
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}