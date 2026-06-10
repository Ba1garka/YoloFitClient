package com.example.yolofitclient.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.repository.AuthRepository
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.AuthNetworkDataSource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.getValue
import com.example.yolofitclient.domain.usecase.CheckAuthFormatUseCase
import com.example.yolofitclient.domain.usecase.CheckAndSaveAuthUseCase
import com.example.yolofitclient.presentation.ui.nav.ProfileRoute
import com.example.yolofitclient.presentation.ui.screen.login.LoginAction
import com.example.yolofitclient.presentation.ui.screen.login.LoginIntent
import com.example.yolofitclient.presentation.ui.screen.login.LoginState


class LoginViewModel : ViewModel() {
    private val checkAuthFormatUseCase by lazy { CheckAuthFormatUseCase() }
    private val checkAndSaveAuthCodeUseCase by lazy {
        CheckAndSaveAuthUseCase(
            AuthRepository(
                authNetworkDataSource = AuthNetworkDataSource(),
                authLocalDataSource = AuthLocalDataSource
            )
        )
    }
    private val _uiState = MutableStateFlow<LoginState>(
        LoginState.Data(
            isEnabledSend = false,
            error = null
        )
    )
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    private val _actionFlow : MutableSharedFlow<LoginAction.OpenScreen> = MutableSharedFlow(
        replay = 1,  // Сохранять последнее значение
        extraBufferCapacity = 0
    )

    val actionFlow = _actionFlow.asSharedFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.Send -> {
                println("AuthViewModel: Send intent with login: ${intent.login}")
                viewModelScope.launch {
                    checkAndSaveAuthCodeUseCase.invoke(intent.login, intent.password).fold(
                        onSuccess = { userDto ->
                            println("AuthViewModel: Auth successful")
                            AuthLocalDataSource.saveUser(userDto)
                            _actionFlow.emit(LoginAction.OpenScreen(ProfileRoute))
                        },
                        onFailure = { error ->
                            println("AuthViewModel: Auth failed: ${error.message}")
                            updateStateIfData { oldState ->
                                oldState.copy(
                                    error = error.message
                                )
                            }
                        }
                    )
                }
            }
            is LoginIntent.TextInput -> {
                updateStateIfData { oldState ->
                    oldState.copy(
                        isEnabledSend = checkAuthFormatUseCase.invoke(
                            intent.login,
                            intent.password
                        ),
                        error = null
                    )
                }
            }
        }
    }

    private fun updateStateIfData(lambda: (LoginState.Data) -> LoginState) {
        _uiState.update { state ->
            (state as? LoginState.Data)?.let { lambda.invoke(it) } ?: state
        }

    }
}