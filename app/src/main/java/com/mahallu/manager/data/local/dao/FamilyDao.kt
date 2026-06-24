package com.mahallu.manager.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.mahallu.manager.data.local.entity.FamilyEntity
import com.mahallu.manager.data.local.entity.FamilyStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {

    @Query("SELECT * FROM families WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllFamilies(): Flow<List<FamilyEntity>>

    @Query("SELECT * FROM families WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllFamiliesPaging(): PagingSource<Int, FamilyEntity>

    @Query("SELECT * FROM families WHERE id = :id")
    suspend fun getFamilyById(id: Long): FamilyEntity?

    @Query("SELECT * FROM families WHERE familyNumber LIKE '%' || :query || '%' OR address LIKE '%' || :query || '%' OR primaryMobile LIKE '%' || :query || '%'")
    fun searchFamilies(query: String): Flow<List<FamilyEntity>>

    @Query("SELECT * FROM families WHERE status = :status AND isArchived = 0")
    fun getFamiliesByStatus(status: FamilyStatus): Flow<List<FamilyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamily(family: FamilyEntity): Long

    @Update
    suspend fun updateFamily(family: FamilyEntity)

    @Delete
    suspend fun deleteFamily(family: FamilyEntity)

    @Query("UPDATE families SET isArchived = 1, archivedAt = :timestamp WHERE id = :id")
    suspend fun archiveFamily(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM families WHERE isArchived = 0")
    fun getTotalFamiliesCount(): Flow<Int>

    @Query("SELECT * FROM families ORDER BY familyNumber ASC")
    suspend fun getAllFamiliesList(): List<FamilyEntity>
}
