package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.WelfareRequest
import com.mahallu.core.database.entity.WelfareStatus
import com.mahallu.core.database.entity.WelfareType
import kotlinx.coroutines.flow.Flow

@Dao
interface WelfareDao {
    @Query("SELECT * FROM welfare ORDER BY requestedDate DESC")
    fun getAllWelfareRequests(): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE status = :status ORDER BY requestedDate DESC")
    fun getWelfareRequestsByStatus(status: WelfareStatus): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE type = :type ORDER BY requestedDate DESC")
    fun getWelfareRequestsByType(type: WelfareType): Flow<List<WelfareRequest>>

    @Query("SELECT * FROM welfare WHERE id = :id")
    suspend fun getWelfareRequestById(id: Long): WelfareRequest?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWelfareRequest(request: WelfareRequest): Long

    @Update
    suspend fun updateWelfareRequest(request: WelfareRequest)

    @Delete
    suspend fun deleteWelfareRequest(request: WelfareRequest)

    @Query("UPDATE welfare SET status = :status, approvedDate = :approvedDate, approvedBy = :approvedBy WHERE id = :id")
    suspend fun approveWelfareRequest(id: Long, status: WelfareStatus, approvedDate: java.util.Date, approvedBy: Long)

    @Query("SELECT SUM(amount) FROM welfare WHERE status = 'DISBURSED' AND strftime('%Y', requestedDate) = :year")
    suspend fun getTotalWelfareDisbursedByYear(year: Int): Double?
}
