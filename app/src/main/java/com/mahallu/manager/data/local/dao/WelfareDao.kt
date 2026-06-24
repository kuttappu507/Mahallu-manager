package com.mahallu.manager.data.local.dao

import androidx.room.*
import com.mahallu.manager.data.local.entity.WelfareRequestEntity
import com.mahallu.manager.data.local.entity.WelfareCategory
import com.mahallu.manager.data.local.entity.WelfareStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface WelfareDao {

    @Query("SELECT * FROM welfare_requests ORDER BY applicationDate DESC")
    fun getAllWelfareRequests(): Flow<List<WelfareRequestEntity>>

    @Query("SELECT * FROM welfare_requests WHERE id = :id")
    suspend fun getWelfareRequestById(id: Long): WelfareRequestEntity?

    @Query("SELECT * FROM welfare_requests WHERE category = :category ORDER BY applicationDate DESC")
    fun getWelfareRequestsByCategory(category: WelfareCategory): Flow<List<WelfareRequestEntity>>

    @Query("SELECT * FROM welfare_requests WHERE status = :status ORDER BY applicationDate DESC")
    fun getWelfareRequestsByStatus(status: WelfareStatus): Flow<List<WelfareRequestEntity>>

    @Query("SELECT * FROM welfare_requests WHERE familyId = :familyId ORDER BY applicationDate DESC")
    fun getWelfareRequestsByFamilyId(familyId: Long): Flow<List<WelfareRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWelfareRequest(request: WelfareRequestEntity): Long

    @Update
    suspend fun updateWelfareRequest(request: WelfareRequestEntity)

    @Delete
    suspend fun deleteWelfareRequest(request: WelfareRequestEntity)

    @Query("UPDATE welfare_requests SET status = :status, approvedBy = :approvedBy, approvedDate = :approvedDate WHERE id = :id")
    suspend fun approveWelfareRequest(
        id: Long,
        status: WelfareStatus,
        approvedBy: Long,
        approvedDate: Long = System.currentTimeMillis()
    )

    @Query("UPDATE welfare_requests SET status = 'DISBURSED', disbursedDate = :disbursedDate WHERE id = :id")
    suspend fun markAsDisbursed(id: Long, disbursedDate: Long = System.currentTimeMillis())

    @Query("SELECT SUM(amount) FROM welfare_requests WHERE status = 'DISBURSED'")
    fun getTotalWelfareAmount(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM welfare_requests WHERE status = 'PENDING'")
    fun getPendingRequestsCount(): Flow<Int>

    @Query("SELECT * FROM welfare_requests WHERE applicationDate >= :startDate AND applicationDate <= :endDate ORDER BY applicationDate DESC")
    fun getWelfareRequestsBetweenDates(startDate: Long, endDate: Long): Flow<List<WelfareRequestEntity>>
}
