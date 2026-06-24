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
    
    @Query("SELECT * FROM deaths WHERE name LIKE '%' || :query || '%' OR fatherName LIKE '%' || :query || '%'")
    fun searchDeaths(query: String): Flow<List<Death>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(death: Death): Long
    
    @Update
    suspend fun update(death: Death)
    
    @Delete
    suspend fun delete(death: Death)
    
    @Query("SELECT COUNT(*) FROM deaths WHERE year(dateOfDeath/1000) = :year")
    fun getDeathCountByYear(year: Int): Flow<Int>
}
