package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Death
import kotlinx.coroutines.flow.Flow

@Dao
interface DeathDao {
    @Query("SELECT * FROM deaths ORDER BY dateOfDeath DESC")
    fun getAllDeaths(): Flow<List<Death>>

    @Query("SELECT * FROM deaths WHERE id = :id")
    suspend fun getDeathById(id: Long): Death?

    @Query("SELECT * FROM deaths WHERE memberId = :memberId ORDER BY dateOfDeath DESC")
    fun getDeathsByMember(memberId: Long): Flow<List<Death>>

    @Query("SELECT * FROM deaths WHERE name LIKE :query OR fatherName LIKE :query")
    fun searchDeaths(query: String): Flow<List<Death>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeath(death: Death): Long

    @Update
    suspend fun updateDeath(death: Death)

    @Delete
    suspend fun deleteDeath(death: Death)

    @Query("SELECT COUNT(*) FROM deaths")
    suspend fun getTotalDeathsCount(): Int
}
