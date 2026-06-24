package com.mahallu.core.database.dao

import androidx.room.*
import androidx.paging.PagingSource
import com.mahallu.core.database.entity.Family
import com.mahallu.core.database.entity.FamilyStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Query("SELECT * FROM families WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllActiveFamilies(): Flow<List<Family>>
    
    @Query("SELECT * FROM families WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getPagingSource(): PagingSource<Int, Family>
    
    @Query("SELECT * FROM families WHERE id = :id")
    suspend fun getFamilyById(id: Long): Family?
    
    @Query("SELECT * FROM families WHERE id = :id")
    fun getFamilyByIdFlow(id: Long): Flow<Family?>
    
    @Query("SELECT * FROM families WHERE familyNumber LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR primaryMobile LIKE '%' || :query || '%'")
    fun searchFamilies(query: String): Flow<List<Family>>
    
    @Query("SELECT * FROM families WHERE status = :status AND isArchived = 0")
    fun getFamiliesByStatus(status: FamilyStatus): Flow<List<Family>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(family: Family): Long
    
    @Update
    suspend fun update(family: Family)
    
    @Delete
    suspend fun delete(family: Family)
    
    @Query("UPDATE families SET isArchived = 1, updatedAt = :timestamp WHERE id = :id")
    suspend fun archiveFamily(id: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT COUNT(*) FROM families WHERE isArchived = 0")
    fun getActiveFamiliesCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM families WHERE isArchived = 1")
    fun getArchivedFamiliesCount(): Flow<Int>
}
