package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Family
import com.mahallu.core.database.entity.FamilyStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
    @Query("SELECT * FROM families ORDER BY createdAt DESC")
    fun getAllFamilies(): Flow<List<Family>>

    @Query("SELECT * FROM families WHERE status = :status ORDER BY createdAt DESC")
    fun getFamiliesByStatus(status: FamilyStatus): Flow<List<Family>>

    @Query("SELECT * FROM families WHERE id = :id")
    suspend fun getFamilyById(id: Long): Family?

    @Query("SELECT * FROM families WHERE familyNumber LIKE :query OR address LIKE :query OR primaryMobile LIKE :query")
    fun searchFamilies(query: String): Flow<List<Family>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFamily(family: Family): Long

    @Update
    suspend fun updateFamily(family: Family)

    @Delete
    suspend fun deleteFamily(family: Family)

    @Query("UPDATE families SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateFamilyStatus(id: Long, status: FamilyStatus, updatedAt: java.util.Date = java.util.Date())

    @Query("SELECT COUNT(*) FROM families")
    suspend fun getTotalFamiliesCount(): Int

    @Query("SELECT COUNT(*) FROM families WHERE status = :status")
    suspend fun getFamiliesCountByStatus(status: FamilyStatus): Int

    @Query("SELECT * FROM families LIMIT :limit OFFSET :offset")
    suspend fun getFamiliesPaged(limit: Int, offset: Int): List<Family>
}
