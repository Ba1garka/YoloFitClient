package com.example.yolofitclient.domain.usecase

import com.example.yolofitclient.data.dto.UserDto
import com.example.yolofitclient.data.repository.UserRepository
import com.example.yolofitclient.domain.entity.UserEntity

class UpdateUserUseCase( private val userRepository: UserRepository) {
    suspend operator fun invoke( user: UserDto ) : Result<UserDto> {
        return userRepository.updateUser(user)
    }
}