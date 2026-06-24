package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.WelfareCategory
import com.mahallu.core.database.entity.WelfareRequest
import com.mahallu.core.database.entity.WelfareStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface WelfareDao {
    @Query("SELECT * FROM welfare ORDER BY createdAt DESC")
    fun getAllWelfareRequests(): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE id = :id")
    suspend fun getWelfareRequestById(id: Long): WelfareRequest?

    @Query("SELECT * FROM welfare WHERE familyId = :familyId ORDER BY createdAt DESC")
    fun getWelfareRequestsByFamily(familyId: Long): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE memberId = :memberId ORDER BY createdAt DESC")
    fun getWelfareRequestsByMember(memberId: Long): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE status = :status ORDER BY createdAt DESC")
    fun getWelfareRequestsByStatus(status: WelfareStatus): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE category = :category ORDER BY createdAt DESC")
    fun getWelfareRequestsByCategory(category: WelfareCategory): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingWelfareRequests(): Flow<List<WelfareRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWelfareRequest(request: WelfareRequest): Long

    @Update
    suspend fun updateWelfareRequest(request: WelfareRequest)

    @Delete
    suspend fun deleteWelfareRequest(request: WelfareRequest)

    @Query("UPDATE welfare SET status = :status, approvedBy = :approvedBy, approvedAt = :approvedAt WHERE id = :id")
    suspend fun approveWelfareRequest(id: Long, status: WelfareStatus, approvedBy: Long?, approvedAt: java.util.Date)

    @Query("UPDATE welfare SET status = :status, disbursedAt = :disbursedAt WHERE id = :id")
    suspend fun markDisbursed(id: Long, status: WelfareStatus, disbursedAt: java.util.Date)

    @Query("SELECT SUM(amount) FROM welfare WHERE status = 'DISBURSED' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalWelfareDisbursed(startDate: Long, endDate: Long): Double?
}
