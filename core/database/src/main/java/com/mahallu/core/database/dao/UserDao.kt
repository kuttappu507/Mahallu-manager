package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.User
import com.mahallu.core.database.entity.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    fun getUsersByRole(role: UserRole): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("UPDATE users SET lastLoginAt = :lastLoginAt WHERE id = :id")
    suspend fun updateLastLogin(id: Long, lastLoginAt: java.util.Date)

    @Query("SELECT COUNT(*) FROM users WHERE isActive = 1")
    suspend fun getActiveUsersCount(): Int
}
