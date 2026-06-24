package com.mahallu.manager.data.local.dao

import androidx.room.*
import com.mahallu.manager.data.local.entity.DeathEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeathDao {

    @Query("SELECT * FROM deaths ORDER BY dateOfDeath DESC")
    fun getAllDeaths(): Flow<List<DeathEntity>>

    @Query("SELECT * FROM deaths WHERE id = :id")
    suspend fun getDeathById(id: Long): DeathEntity?

    @Query("SELECT * FROM deaths WHERE registrationNumber = :registrationNumber")
    suspend fun getDeathByRegistrationNumber(registrationNumber: String): DeathEntity?

    @Query("SELECT * FROM deaths WHERE deceasedName LIKE '%' || :query || '%' OR fatherName LIKE '%' || :query || '%' OR registrationNumber LIKE '%' || :query || '%'")
    fun searchDeaths(query: String): Flow<List<DeathEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeath(death: DeathEntity): Long

    @Update
    suspend fun updateDeath(death: DeathEntity)

    @Delete
    suspend fun deleteDeath(death: DeathEntity)

    @Query("UPDATE deaths SET certificateGenerated = 1 WHERE id = :id")
    suspend fun markCertificateGenerated(id: Long)

    @Query("SELECT COUNT(*) FROM deaths")
    fun getTotalDeathsCount(): Flow<Int>

    @Query("SELECT * FROM deaths WHERE dateOfDeath >= :startDate AND dateOfDeath <= :endDate ORDER BY dateOfDeath DESC")
    fun getDeathsBetweenDates(startDate: Long, endDate: Long): Flow<List<DeathEntity>>
}
