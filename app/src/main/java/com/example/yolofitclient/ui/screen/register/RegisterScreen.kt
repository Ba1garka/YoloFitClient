package com.example.yolofitclient.ui.screen.register

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.ui.theme.AuthColors
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape
import com.example.yolofitclient.R

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("male") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var fitnessLevel by remember { mutableStateOf("beginner") }
    var goal by remember {mutableStateOf("GAIN_WEIGHT")}

    var currentStep by remember { mutableIntStateOf(0) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val totalSteps = 3

    val genders = listOf("male", "female")
    val fitnessLevels = listOf("beginner", "intermediate", "advanced")
    var showFitnessDropdown by remember { mutableStateOf(false) }
    val goals = listOf("LOSE_WEIGHT","GAIN_WEIGHT","BUILD_MUSCLE")
    var showGoalsDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(state) {
        if (state is RegisterState.Content) {
            val userEntity = (state as RegisterState.Content).user

            val userDto = UserDto(
                id = userEntity.id,
                name = userEntity.name,
                email = userEntity.email,
                birthDate = userEntity.birthDate,
                gender = userEntity.gender,
                height = userEntity.height,
                weight = userEntity.weight,
                fitnessLevel = userEntity.fitnessLevel,
                photoUrl = userEntity.photoUrl,
                goal = userEntity.goal,
                dailyCalorieTarget = userEntity.dailyCalorieTarget
            )

            if (userDto.name != null){
                AuthLocalDataSource.setToken(userDto.name, password)
            }

            AuthLocalDataSource.saveUser(userDto)
            onRegisterSuccess()
        }
    }

    when (val currentState = state) {
        RegisterState.Initial -> {
            RegisterContentState(
                name = name,
                onNameChange = {
                    name = it
                    validationError = null
                },
                email = email,
                onEmailChange = {
                    email = it
                    validationError = null
                },
                password = password,
                onPasswordChange = {
                    password = it
                    validationError = null
                },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = {
                    confirmPassword = it
                    validationError = null
                },
                passwordVisible = passwordVisible,
                onPasswordToggle = { passwordVisible = !passwordVisible },
                birthDate = birthDate,
                onBirthDateChange = { birthDate = it },
                gender = gender,
                onGenderChange = { gender = it },
                height = height,
                onHeightChange = { height = it },
                weight = weight,
                onWeightChange = { weight = it },
                fitnessLevel = fitnessLevel,
                onFitnessLevelChange = { fitnessLevel = it },
                currentStep = currentStep,
                onStepChange = {
                    currentStep = it
                    validationError = null
                },
                totalSteps = totalSteps,
                validationError = validationError,
                genders = genders,
                fitnessLevels = fitnessLevels,
                showFitnessDropdown = showFitnessDropdown,
                onFitnessDropdownToggle = { showFitnessDropdown = it },
                goal = goal,
                onGoalsChange = { goal = it },
                goals = goals,
                showGoalsDropdown = showGoalsDropdown,
                onGoalsDropdownToggle = { showGoalsDropdown = it },
                onRegisterClick = {
                    if (currentStep == 2) {
                        viewModel.validateAndRegister(
                            birthDate = birthDate,
                            gender = gender,
                            height = height,
                            weight = weight,
                            fitnessLevel = fitnessLevel,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            name = name,
                            goal = goal,
                            onValidationError = { errorMessage ->
                                validationError = errorMessage
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        )
                    } else {
                        currentStep++
                    }
                },
                onLoginClick = { onLoginClick() },
            )
        }

        RegisterState.Loading -> {
            LoadingState()
        }

        is RegisterState.Error -> {
            RegisterErrorState(
                errorMessage = currentState.reason,
                onRetry = {
                    viewModel.validateAndRegister(
                        birthDate = birthDate,
                        gender = gender,
                        height = height,
                        weight = weight,
                        fitnessLevel = fitnessLevel,
                        email = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        name = name,
                        goal = goal,
                        onValidationError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        },
                    )
                },
                onBackToForm = { viewModel.resetState() }
            )
        }

        is RegisterState.Content -> {
            SuccessState(userName = currentState.user.name)
        }
    }
}

@Composable
private fun RegisterContentState(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordToggle: () -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    fitnessLevel: String,
    onFitnessLevelChange: (String) -> Unit,
    currentStep: Int,
    onStepChange: (Int) -> Unit,
    totalSteps: Int,
    validationError: String?,
    genders: List<String>,
    fitnessLevels: List<String>,
    showFitnessDropdown: Boolean,
    onFitnessDropdownToggle: (Boolean) -> Unit,
    goal : String,
    onGoalsChange : (String) -> Unit,
    goals : List<String>,
    showGoalsDropdown : Boolean,
    onGoalsDropdownToggle : (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(AuthColors.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuthColors.AccentGreen.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.5f, size.height * 0.3f),
                    radius = size.minDimension * 0.7f
                ),
                radius = size.minDimension * 0.7f,
                center = Offset(size.width * 0.5f, size.height * 0.3f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp)
        ) {
            IconButton(
                onClick = {
                    if (currentStep > 0) {
                        onStepChange(currentStep - 1)
                    } else {
                        onLoginClick()
                    }
                },
                modifier = Modifier.clip(CircleShape).background(AuthColors.FieldBackground)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Назад",
                    tint = AuthColors.AccentGreen
                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.register2),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = AuthColors.TextPrimary,
                    letterSpacing = 2.sp,
                    fontSize = 36.sp
                )
            )

            Text(
                text = when (currentStep) {
                    0 -> stringResource(R.string.basicInfo)
                    1 -> stringResource(R.string.phyParams)
                    2 -> stringResource(R.string.safety)
                    else -> ""
                },
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = AuthColors.AccentGreen,
                    letterSpacing = 1.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(totalSteps) { step ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (step <= currentStep) AuthColors.AccentGreen
                                else AuthColors.FieldBackground
                            )
                    )
                }
            }

            AnimatedVisibility(
                visible = validationError != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                validationError?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFF4444).copy(alpha = 0.1f)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFFF4444).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color(0xFFFF4444),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = error,
                                color = Color(0xFFFF4444),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState > initialState) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                label = "stepTransition"
            ) { step ->
                when (step) {
                    0 -> StepBasicInfo(
                        name = name,
                        onNameChange = onNameChange,
                        email = email,
                        onEmailChange = onEmailChange,
                        birthDate = birthDate,
                        onBirthDateChange = onBirthDateChange
                    )
                    1 -> StepPhysicalParams(
                        gender = gender,
                        onGenderChange = onGenderChange,
                        height = height,
                        onHeightChange = onHeightChange,
                        weight = weight,
                        onWeightChange = onWeightChange,
                        fitnessLevel = fitnessLevel,
                        onFitnessLevelChange = onFitnessLevelChange,
                        genders = genders,
                        fitnessLevels = fitnessLevels,
                        showFitnessDropdown = showFitnessDropdown,
                        onFitnessDropdownToggle = onFitnessDropdownToggle,
                        goal = goal,
                        onGoalsChange = onGoalsChange,
                        goals = goals,
                        showGoalsDropdown = showGoalsDropdown,
                        onGoalsDropdownToggle = onGoalsDropdownToggle,
                    )
                    2 -> StepPassword(
                        password = password,
                        onPasswordChange = onPasswordChange,
                        confirmPassword = confirmPassword,
                        onConfirmPasswordChange = onConfirmPasswordChange,
                        passwordVisible = passwordVisible,
                        onPasswordToggle = onPasswordToggle
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (currentStep > 0) {
                    OutlinedButton(
                        onClick = { onStepChange(currentStep - 1) },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = DiagonalRoundedCornerShape(
                            topLeft = 16f,
                            topRight = 40f,
                            bottomRight = 16f,
                            bottomLeft = 40f
                        ),
                        border = BorderStroke(1.dp, AuthColors.AccentGreen)
                    ) {
                        Text(stringResource(R.string.back), color = AuthColors.AccentGreen)
                    }
                }

                Button(
                    onClick = onRegisterClick,
                    enabled = when {
                        currentStep == 0 -> name.isNotEmpty() && email.isNotEmpty()
                        currentStep == 1 -> height.isNotEmpty() && weight.isNotEmpty()
                        currentStep == 2 -> password.isNotEmpty() &&
                                confirmPassword.isNotEmpty() &&
                                password == confirmPassword &&
                                password.length >= 6
                        else -> false
                    },
                    modifier = Modifier
                        .weight(if (currentStep > 0) 1f else 2f)
                        .height(56.dp),
                    shape = DiagonalRoundedCornerShape(
                        topLeft = if (currentStep > 0) 16f else 40f,
                        topRight = if (currentStep > 0) 40f else 16f,
                        bottomRight = if (currentStep > 0) 16f else 40f,
                        bottomLeft = if (currentStep > 0) 40f else 16f
                    ),
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
                                Brush.linearGradient(
                                    colors = listOf(
                                        AuthColors.AccentGreen,
                                        AuthColors.AccentGreenDark
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(Float.POSITIVE_INFINITY, 0f)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (currentStep < totalSteps - 1) stringResource(R.string.next) else stringResource(R.string.create),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = AuthColors.Background,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepBasicInfo(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    birthDate: String,
    onBirthDateChange: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AuthTextField(
            value = name,
            onValueChange = onNameChange,
            placeholder = stringResource(R.string.name),
            icon = Icons.Default.Person
        )

        AuthTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.email),
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        AuthTextField(
            value = birthDate,
            onValueChange = onBirthDateChange,
            placeholder = stringResource(R.string.birthday) + " " + stringResource(R.string.placeholderData),
            icon = Icons.Default.CalendarMonth
        )
    }
}

@Composable
private fun StepPhysicalParams(
    gender: String,
    onGenderChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit,
    fitnessLevel: String,
    onFitnessLevelChange: (String) -> Unit,
    genders: List<String>,
    fitnessLevels: List<String>,
    showFitnessDropdown: Boolean,
    onFitnessDropdownToggle: (Boolean) -> Unit,
    goal : String,
    onGoalsChange : (String) -> Unit,
    goals: List<String>,
    showGoalsDropdown : Boolean,
    onGoalsDropdownToggle : (Boolean) -> Unit,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.gender),
            style = MaterialTheme.typography.labelLarge.copy(
                color = AuthColors.TextSecondary,
                letterSpacing = 1.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            genders.forEach { genderOption ->
                val isSelected = gender == genderOption

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(DiagonalRoundedCornerShape(
                            topLeft = 30f,
                            topRight = 12f,
                            bottomRight = 30f,
                            bottomLeft = 12f
                        ))
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        AuthColors.AccentGreen.copy(alpha = 0.3f),
                                        AuthColors.AccentGreenDark.copy(alpha = 0.2f)
                                    ),
                                    start = Offset(0f, 0f),
                                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(
                                        AuthColors.FieldBackground,
                                        AuthColors.FieldBackground
                                    )
                                )
                            }
                        )
                        .border(
                            1.dp,
                            if (isSelected) AuthColors.AccentGreen
                            else AuthColors.CardBorder,
                            DiagonalRoundedCornerShape(
                                topLeft = 30f,
                                topRight = 12f,
                                bottomRight = 30f,
                                bottomLeft = 12f
                            )
                        )
                        .clickable { onGenderChange(genderOption) }
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = genderOption,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AuthColors.AccentGreen
                            else AuthColors.TextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        AuthTextField(
            value = height,
            onValueChange = onHeightChange,
            placeholder = stringResource(R.string.height) + " (см)",
            icon = Icons.Default.Height,
            keyboardType = KeyboardType.Number
        )

        AuthTextField(
            value = weight,
            onValueChange = onWeightChange,
            placeholder = stringResource(R.string.weight) + " (кг)",
            icon = Icons.Default.MonitorWeight,
            keyboardType = KeyboardType.Number
        )

        Text(
            text = stringResource(R.string.trainingLevel),
            style = MaterialTheme.typography.labelLarge.copy(
                color = AuthColors.TextSecondary,
                letterSpacing = 1.sp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AuthColors.FieldBackground)
                .clickable { onFitnessDropdownToggle(true) }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text( text = fitnessLevel, color = AuthColors.AccentGreen)
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen
                )
            }

            DropdownMenu(
                expanded = showFitnessDropdown,
                onDismissRequest = { onFitnessDropdownToggle(false) },
                modifier = Modifier.fillMaxWidth(0.8f)
                    .background(AuthColors.FieldBackground)
            ) {
                fitnessLevels.forEach { level ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                level,
                                color = if (fitnessLevel == level)
                                    AuthColors.AccentGreen
                                else AuthColors.TextSecondary
                            )
                        },
                        onClick = {
                            onFitnessLevelChange(level)
                            onFitnessDropdownToggle(false)
                        }
                    )
                }
            }
        }

        Text(
            text = stringResource(R.string.YourTarget),
            style = MaterialTheme.typography.labelLarge.copy(
                color = AuthColors.TextSecondary,
                letterSpacing = 1.sp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(AuthColors.FieldBackground)
                .clickable { onGoalsDropdownToggle(true) }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text( text = goal, color = AuthColors.AccentGreen)
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen
                )
            }

            DropdownMenu(
                expanded = showGoalsDropdown,
                onDismissRequest = { onGoalsDropdownToggle(false) },
                modifier = Modifier.fillMaxWidth(0.8f).background(AuthColors.FieldBackground)
            ) {
                goals.forEach { goalN ->
                    DropdownMenuItem(
                        text = {
                            Text(goalN, color = if (goal == goalN) AuthColors.AccentGreen else AuthColors.TextSecondary)
                        },
                        onClick = {
                            onGoalsChange(goalN)
                            onGoalsDropdownToggle(false)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StepPassword(
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordToggle: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AuthTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = stringResource(R.string.password),
            icon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggle = onPasswordToggle
        )

        AuthTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = stringResource(R.string.password2),
            icon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggle = onPasswordToggle
        )

        if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (password != confirmPassword) {
                Text(
                    text = stringResource(R.string.passwordError),
                    color = Color(0xFFFF4444),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            } else if (password.length < 6) {
                Text(
                    text = stringResource(R.string.error),
                    color = Color(0xFFFF4444),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize().background(AuthColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = AuthColors.AccentGreen
            )
            Text(
                text = stringResource(R.string.processCreate),
                color = AuthColors.TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun RegisterErrorState(
    errorMessage: String,
    onRetry: () -> Unit,
    onBackToForm: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(AuthColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                tint = Color(0xFFFF4444),
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = errorMessage,
                color = AuthColors.TextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackToForm,
                    border = BorderStroke(1.dp, AuthColors.AccentGreen)
                ) {
                    Text(stringResource(R.string.changeData), color = AuthColors.AccentGreen)
                }

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AuthColors.AccentGreenDark
                    )
                ) {
                    Text(stringResource(R.string.repeat))
                }
            }
        }
    }
}

@Composable
private fun SuccessState(userName: String) {
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
                text = stringResource(R.string.welcome) + ", $userName!",
                color = AuthColors.TextPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = stringResource(R.string.createAccSuccess),
                color = AuthColors.TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(AuthColors.FieldBackground)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                AuthColors.AccentGreen.copy(alpha = 0.2f),
                                AuthColors.AccentGreenDark.copy(alpha = 0.1f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = AuthColors.AccentGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))

            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(placeholder, color = AuthColors.TextDim)
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = AuthColors.AccentGreen,
                    focusedTextColor = AuthColors.TextPrimary,
                    unfocusedTextColor = AuthColors.TextPrimary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                visualTransformation = if (isPassword && !passwordVisible) {
                    PasswordVisualTransformation()
                } else {
                    VisualTransformation.None
                },
                trailingIcon = if (isPassword) {
                    {
                        IconButton(onClick = onPasswordToggle) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = AuthColors.TextDim
                            )
                        }
                    }
                } else null,
                singleLine = true
            )
        }
    }
}