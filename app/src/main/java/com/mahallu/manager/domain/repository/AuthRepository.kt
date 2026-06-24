package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): User?
    suspend fun createUser(user: User)
    suspend fun updateUser(user: User)
    suspend fun deleteUser(user: User)
    fun getAllUsers(): Flow<List<User>>
    suspend fun getUserByUsername(username: String): User?
    suspend fun changePassword(username: String, newPassword: String)
}
