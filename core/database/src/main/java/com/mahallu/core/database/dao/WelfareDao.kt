package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.WelfareRequest
import com.mahallu.core.database.entity.WelfareCategory
import com.mahallu.core.database.entity.ApprovalStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface WelfareDao {
    @Query("SELECT * FROM welfare_requests ORDER BY createdAt DESC")
    fun getAllWelfareRequests(): Flow<List<WelfareRequest>>
    
    @Query("SELECT * FROM welfare_requests WHERE id = :id")
    suspend fun getWelfareRequestById(id: Long): WelfareRequest?
    
    @Query("SELECT * FROM welfare_requests WHERE category = :category ORDER BY createdAt DESC")
    fun getWelfareRequestsByCategory(category: WelfareCategory): Flow<List<WelfareRequest>>
    
    @Query("SELECT * FROM welfare_requests WHERE approvalStatus = :status ORDER BY createdAt DESC")
    fun getWelfareRequestsByStatus(status: ApprovalStatus): Flow<List<WelfareRequest>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: WelfareRequest): Long
    
    @Update
    suspend fun update(request: WelfareRequest)
    
    @Delete
    suspend fun delete(request: WelfareRequest)
    
    @Query("UPDATE welfare_requests SET approvalStatus = :status, approvedBy = :approvedBy, disbursedDate = :disbursedDate WHERE id = :id")
    suspend fun approveRequest(id: Long, status: ApprovalStatus, approvedBy: Long?, disbursedDate: Long?)
    
    @Query("SELECT SUM(amount) FROM welfare_requests WHERE approvalStatus = :status")
    fun getTotalDisbursed(status: ApprovalStatus): Flow<Double?>
}
