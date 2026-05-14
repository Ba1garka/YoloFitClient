package com.example.yolofitclient.ui.screen.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.yolofitclient.ui.screen.register.AuthTextField
import com.example.yolofitclient.ui.theme.AuthColors
import com.example.yolofitclient.ui.theme.DiagonalRoundedCornerShape


@Composable
fun LoginScreen(
    navController : NavController,
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit,
    toRegister : () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when(action){
                is LoginAction.OpenScreen -> {
                    navController.navigate(action.route)
//                    onLoginSuccess()
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(AuthColors.Background)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuthColors.AccentGreen.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.8f, size.height * 0.1f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(size.width * 0.8f, size.height * 0.1f)
            )

            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        AuthColors.AccentGreenDark.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 0.2f, size.height * 0.9f),
                    radius = size.minDimension * 0.5f
                ),
                radius = size.minDimension * 0.5f,
                center = Offset(size.width * 0.2f, size.height * 0.9f)
            )
        }

        when(val currentState = state){
            is LoginState.Loading -> LoadingState()
            is LoginState.Data -> ContentState(onLoginSuccess, toRegister, currentState, viewModel)
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
                text = "Загрузка...",
                color = AuthColors.TextSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ContentState(
    onLoginSuccess : () -> Unit,
    toRegister : () -> Unit,
    currentState : LoginState.Data,
    viewModel: LoginViewModel
){
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            AuthColors.AccentGreen,
                            AuthColors.AccentGreenDark
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = AuthColors.Background,
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "YOLO FIT",
            style = MaterialTheme.typography.displayLarge.copy(
                fontWeight = FontWeight.Black,
                color = AuthColors.TextPrimary,
                letterSpacing = 4.sp,
                fontSize = 40.sp
            )
        )

        Text(
            text = "Добро пожаловать",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = AuthColors.AccentGreen,
                letterSpacing = 2.sp
            )
        )
        Spacer(modifier = Modifier.height(48.dp))

        AuthTextField(
            value = name,
            onValueChange = {
                name = it
                viewModel.onIntent(LoginIntent.TextInput(name, password))
                            },
            placeholder = "Email",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = {
                password = it
                viewModel.onIntent(LoginIntent.TextInput(name, password))
                            },
            placeholder = "Пароль",
            icon = Icons.Default.Lock,
            keyboardType = KeyboardType.Password,
            isPassword = true,
            passwordVisible = passwordVisible,
            onPasswordToggle = { passwordVisible = !passwordVisible }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                println("LoginScreen: Button clicked, login=$name, password=$password")
                viewModel.onIntent(LoginIntent.Send(name, password))

            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = currentState.isEnabledSend,
            shape = DiagonalRoundedCornerShape(
                topLeft = 40f,
                topRight = 16f,
                bottomRight = 40f,
                bottomLeft = 16f
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
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
                    text = "ВОЙТИ",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = AuthColors.Background,
                        letterSpacing = 2.sp
                    )
                )
            }
        }
        if (currentState.error != null){
            Text(
                modifier = Modifier,
                text = currentState.error,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red
            )
        }
        Spacer(modifier = Modifier.height(24.dp))

        TextButton(
            onClick = toRegister
        ) {
            Text(
                text = "Нет аккаунта? ",
                color = AuthColors.TextSecondary
            )
            Text(
                text = "Зарегистрироваться",
                color = AuthColors.AccentGreen,
                fontWeight = FontWeight.Bold
            )
        }
    }
}