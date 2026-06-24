package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Marriage
import kotlinx.coroutines.flow.Flow

@Dao
interface MarriageDao {
    @Query("SELECT * FROM marriages ORDER BY nikahDate DESC")
    fun getAllMarriages(): Flow<List<Marriage>>

    @Query("SELECT * FROM marriages WHERE id = :id")
    suspend fun getMarriageById(id: Long): Marriage?

    @Query("SELECT * FROM marriages WHERE brideFamilyId = :familyId OR groomFamilyId = :familyId ORDER BY nikahDate DESC")
    fun getMarriagesByFamily(familyId: Long): Flow<List<Marriage>>

    @Query("SELECT * FROM marriages WHERE registrationNumber = :regNum")
    suspend fun getMarriageByRegistrationNumber(regNum: String): Marriage?

    @Query("SELECT * FROM marriages WHERE certificateGenerated = 0 ORDER BY nikahDate DESC")
    fun getMarriagesWithoutCertificate(): Flow<List<Marriage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarriage(marriage: Marriage): Long

    @Update
    suspend fun updateMarriage(marriage: Marriage)

    @Delete
    suspend fun deleteMarriage(marriage: Marriage)

    @Query("UPDATE marriages SET certificateGenerated = 1 WHERE id = :id")
    suspend fun markCertificateGenerated(id: Long)

    @Query("SELECT COUNT(*) FROM marriages")
    suspend fun countMarriages(): Int
}
