package com.example.yolofitclient.presentation.viewmodel


import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.UserRepository
import com.example.yolofitclient.data.source.AuthNetworkDataSource
import com.example.yolofitclient.domain.usecase.RegisterUseCase
import com.example.yolofitclient.presentation.ui.screen.register.RegisterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class RegisterViewModel: ViewModel() {
    private val registerUseCase = RegisterUseCase(
        userRepository = UserRepository(AuthNetworkDataSource())
    )
    private val _uiState: MutableStateFlow<RegisterState> = MutableStateFlow(RegisterState.Initial)
    val uiState = _uiState.asStateFlow()

    fun validateAndRegister(
        email: String,
        password: String,
        confirmPassword: String,
        name: String,
        birthDate: String,
        gender: String,
        height: String,
        weight: String,
        fitnessLevel: String,
        goal: String,
        onValidationError: (String) -> Unit
    ) {
        when {
            name.isEmpty() -> {
                onValidationError("Укажите имя")
                return
            }
            name.length < 2 -> {
                onValidationError("Имя должно содержать минимум 2 символа")
                return
            }
            email.isEmpty() -> {
                onValidationError("Укажите email")
                return
            }
            !isValidEmail(email) -> {
                onValidationError("Некорректный email")
                return
            }
            password.isEmpty() -> {
                onValidationError("Укажите пароль")
                return
            }
            password.length < 6 -> {
                onValidationError("Пароль должен содержать минимум 6 символов")
                return
            }
            confirmPassword.isEmpty() -> {
                onValidationError("Подтвердите пароль")
                return
            }
            password != confirmPassword -> {
                onValidationError("Пароли должны совпадать")
                return
            }
            birthDate.isNotEmpty() && !isValidDate(birthDate) -> {
                onValidationError("Некорректная дата рождения (ГГГГ-ММ-ДД)")
                return
            }
            gender.isEmpty() -> {
                onValidationError("Укажите пол")
                return
            }
            height.isEmpty() -> {
                onValidationError("Укажите рост")
                return
            }
            height.toDoubleOrNull() == null || height.toDouble() <= 0 -> {
                onValidationError("Некорректный рост")
                return
            }
            weight.isEmpty() -> {
                onValidationError("Укажите вес")
                return
            }
            weight.toDoubleOrNull() == null || weight.toDouble() <= 0 -> {
                onValidationError("Некорректный вес")
                return
            }
            fitnessLevel.isEmpty() -> {
                onValidationError("Укажите уровень подготовки")
                return
            }
        }

        register(
            name,
            email,
            birthDate,
            gender,
            height,
            weight,
            fitnessLevel,
            password,
            goal
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") &&
                email.contains(".") &&
                email.length >= 5 &&
                Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidDate(date: String): Boolean {
        val dateRegex = Regex("""^\d{4}-\d{2}-\d{2}$""")
        if (!dateRegex.matches(date)) return false

        try {
            val parts = date.split("-")
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()

            if (month < 1 || month > 12) return false
            if (day < 1 || day > 31) return false
            if (year < 1900 || year > 2026) return false

            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun register(
        name: String,
        email: String,
        birthDate: String,
        gender: String,
        height: String,
        weight: String,
        fitnessLevel: String,
        password: String,
        goal: String
    ){
        viewModelScope.launch {
            _uiState.emit(RegisterState.Loading)

            registerUseCase.invoke(
                name,
                email,
                birthDate,
                gender,
                height,
                weight,
                fitnessLevel,
                password,
                goal
            ).fold(
                onSuccess = { user ->
                    _uiState.emit(RegisterState.Content(user))
                },
                onFailure = { error ->
                    _uiState.emit(RegisterState.Error(error.message.orEmpty()))
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = RegisterState.Initial
    }
}

