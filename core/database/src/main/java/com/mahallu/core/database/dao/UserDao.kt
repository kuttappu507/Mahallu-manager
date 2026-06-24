package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users WHERE isActive = 1 ORDER BY name")
    fun getAllActiveUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = :role AND isActive = 1")
    fun getUsersByRole(role: com.mahallu.core.database.entity.UserRole): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("UPDATE users SET lastLoginAt = :lastLoginAt WHERE id = :id")
    suspend fun updateLastLogin(id: Long, lastLoginAt: java.util.Date)

    @Query("SELECT COUNT(*) FROM users WHERE isActive = 1")
    suspend fun countActiveUsers(): Int
}
