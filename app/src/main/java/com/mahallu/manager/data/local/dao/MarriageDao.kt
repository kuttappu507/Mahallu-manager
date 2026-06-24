package com.mahallu.manager.data.local.dao

import androidx.room.*
import com.mahallu.manager.data.local.entity.MarriageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MarriageDao {

    @Query("SELECT * FROM marriages ORDER BY nikahDate DESC")
    fun getAllMarriages(): Flow<List<MarriageEntity>>

    @Query("SELECT * FROM marriages WHERE id = :id")
    suspend fun getMarriageById(id: Long): MarriageEntity?

    @Query("SELECT * FROM marriages WHERE registrationNumber = :registrationNumber")
    suspend fun getMarriageByRegistrationNumber(registrationNumber: String): MarriageEntity?

    @Query("SELECT * FROM marriages WHERE brideName LIKE '%' || :query || '%' OR groomName LIKE '%' || :query || '%' OR registrationNumber LIKE '%' || :query || '%'")
    fun searchMarriages(query: String): Flow<List<MarriageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarriage(marriage: MarriageEntity): Long

    @Update
    suspend fun updateMarriage(marriage: MarriageEntity)

    @Delete
    suspend fun deleteMarriage(marriage: MarriageEntity)

    @Query("UPDATE marriages SET certificateGenerated = 1 WHERE id = :id")
    suspend fun markCertificateGenerated(id: Long)

    @Query("SELECT COUNT(*) FROM marriages")
    fun getTotalMarriagesCount(): Flow<Int>

    @Query("SELECT * FROM marriages WHERE nikahDate >= :startDate AND nikahDate <= :endDate ORDER BY nikahDate DESC")
    fun getMarriagesBetweenDates(startDate: Long, endDate: Long): Flow<List<MarriageEntity>>
}
