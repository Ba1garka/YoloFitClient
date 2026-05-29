package com.example.yolofitclient.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.repository.UserRepository
import com.example.yolofitclient.data.source.AuthLocalDataSource
import com.example.yolofitclient.data.source.AuthNetworkDataSource
import com.example.yolofitclient.domain.usecase.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel: ViewModel() {

    private val updateUserUseCase = UpdateUserUseCase(userRepository = UserRepository(AuthNetworkDataSource()))
    private val _uiState : MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState.Content)
    val uiState = _uiState.asStateFlow()

    fun updateUser(user: UserDto){
        viewModelScope.launch {
            _uiState.emit(ProfileState.Loading)

            updateUserUseCase.invoke(user).fold(
                onSuccess = { userDto ->
                    AuthLocalDataSource.saveUser(userDto)
                    println("Profile. user.dailyCalorieTarget: " + user.dailyCalorieTarget)
                    _uiState.emit(ProfileState.Success)
                },
                onFailure = { error ->
                    _uiState.emit(ProfileState.Error(error.message.toString()))
                }
            )
        }
    }
}