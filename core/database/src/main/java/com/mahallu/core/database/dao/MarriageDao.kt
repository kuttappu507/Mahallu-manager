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
    
    @Query("SELECT * FROM marriages WHERE registrationNumber LIKE '%' || :query || '%' OR brideName LIKE '%' || :query || '%' OR groomName LIKE '%' || :query || '%'")
    fun searchMarriages(query: String): Flow<List<Marriage>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(marriage: Marriage): Long
    
    @Update
    suspend fun update(marriage: Marriage)
    
    @Delete
    suspend fun delete(marriage: Marriage)
    
    @Query("SELECT COUNT(*) FROM marriages WHERE year(nikahDate/1000) = :year")
    fun getMarriageCountByYear(year: Int): Flow<Int>
}
