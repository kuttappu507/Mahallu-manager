package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.UserDao
import com.mahallu.manager.data.local.entity.UserEntity
import com.mahallu.manager.domain.model.User
import com.mahallu.manager.domain.repository.AuthRepository
import com.mahallu.manager.core.security.SecurityManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val securityManager: SecurityManager
) : AuthRepository {

    override suspend fun login(username: String, password: String): User? {
        val userEntity = userDao.getUserByUsername(username)
        return if (userEntity != null && securityManager.verifyPassword(password, userEntity.passwordHash)) {
            userEntity.toDomainModel()
        } else {
            null
        }
    }

    override suspend fun createUser(user: User) {
        val entity = user.toEntity().copy(
            passwordHash = securityManager.hashPassword("password123")
        )
        userDao.insertUser(entity)
    }

    override suspend fun updateUser(user: User) {
        userDao.updateUser(user.toEntity())
    }

    override suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)?.toDomainModel()
    }

    override suspend fun changePassword(username: String, newPassword: String) {
        val user = userDao.getUserByUsername(username)
        user?.let {
            val updatedUser = it.copy(passwordHash = securityManager.hashPassword(newPassword))
            userDao.updateUser(updatedUser)
        }
    }
}
