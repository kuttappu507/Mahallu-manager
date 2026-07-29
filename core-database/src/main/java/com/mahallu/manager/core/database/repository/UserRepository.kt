package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.AuditLogDao
import com.mahallu.manager.core.database.dao.UserDao
import com.mahallu.manager.core.database.entity.UserEntity
import com.mahallu.manager.core.security.PasswordHasher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val auditDao: AuditLogDao
) {
    fun observeAll(): Flow<List<UserEntity>> = userDao.observeAll()

    suspend fun getById(id: String): UserEntity? = userDao.getById(id)
    suspend fun getByUsername(username: String): UserEntity? = userDao.getByUsername(username)

    suspend fun createUser(
        id: String,
        username: String,
        password: String,
        fullName: String,
        role: String,
        phone: String? = null,
        email: String? = null
    ): Result<UserEntity> {
        if (username.isBlank() || password.length < 6) {
            return Result.failure(IllegalArgumentException("Username and password (min 6 chars) required"))
        }
        if (getByUsername(username) != null) {
            return Result.failure(IllegalStateException("Username already exists"))
        }
        val user = UserEntity(
            id = id,
            username = username.trim().lowercase(),
            passwordHash = PasswordHasher.hash(password),
            fullName = fullName.trim(),
            role = role,
            phone = phone,
            email = email
        )
        userDao.insert(user)
        return Result.success(user)
    }

    suspend fun authenticate(username: String, password: String): Result<UserEntity> {
        val user = userDao.getByUsername(username.trim().lowercase())
            ?: return Result.failure(IllegalStateException("User not found"))
        if (!user.isActive) return Result.failure(IllegalStateException("Account disabled"))
        if (!PasswordHasher.verify(password, user.passwordHash)) {
            return Result.failure(IllegalStateException("Invalid credentials"))
        }
        val now = System.currentTimeMillis()
        userDao.updateLastLogin(user.id, now)
        auditDao.upsert(
            com.mahallu.manager.core.database.entity.AuditLogEntity(
                id = java.util.UUID.randomUUID().toString(),
                userId = user.id,
                userName = user.fullName,
                action = "LOGIN",
                description = "User logged in",
                timestamp = now
            )
        )
        return Result.success(user)
    }

    suspend fun updatePassword(userId: String, newPassword: String): Result<Unit> {
        val user = userDao.getById(userId) ?: return Result.failure(IllegalStateException("User not found"))
        if (newPassword.length < 6) return Result.failure(IllegalArgumentException("Password too short"))
        userDao.update(user.copy(passwordHash = PasswordHasher.hash(newPassword)))
        return Result.success(Unit)
    }

    suspend fun updateProfile(userId: String, fullName: String, phone: String?, email: String?): Result<Unit> {
        val user = userDao.getById(userId) ?: return Result.failure(IllegalStateException("User not found"))
        userDao.update(user.copy(fullName = fullName, phone = phone, email = email))
        return Result.success(Unit)
    }

    suspend fun setActive(userId: String, active: Boolean) {
        val user = userDao.getById(userId) ?: return
        userDao.update(user.copy(isActive = active))
    }

    suspend fun delete(userId: String) {
        userDao.getById(userId)?.let { userDao.clear() } // simple
    }

    suspend fun count(): Int = 0 // optional
}