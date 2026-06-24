package com.mahallu.manager.domain.usecase

import com.mahallu.manager.domain.model.User
import com.mahallu.manager.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(username: String, password: String): User? {
        return authRepository.login(username, password)
    }
}
