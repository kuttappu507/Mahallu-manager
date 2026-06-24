package com.mahallu.manager.data.local.dao

import androidx.room.*
import com.mahallu.manager.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {

    @Query("SELECT * FROM subscriptions ORDER BY paymentDate DESC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE familyId = :familyId ORDER BY paymentDate DESC")
    fun getSubscriptionsByFamilyId(familyId: Long): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): SubscriptionEntity?

    @Query("SELECT * FROM subscriptions WHERE receiptNumber = :receiptNumber")
    suspend fun getSubscriptionByReceiptNumber(receiptNumber: String): SubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: SubscriptionEntity): Long

    @Update
    suspend fun updateSubscription(subscription: SubscriptionEntity)

    @Delete
    suspend fun deleteSubscription(subscription: SubscriptionEntity)

    @Query("SELECT SUM(amount) FROM subscriptions WHERE isPaid = 1")
    fun getTotalCollectionAmount(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM subscriptions WHERE strftime('%m', paymentDate/1000, 'unixepoch') = strftime('%m', 'now') AND isPaid = 1")
    fun getCurrentMonthCollection(): Flow<Double?>

    @Query("SELECT * FROM subscriptions WHERE paymentDate >= :startDate AND paymentDate <= :endDate ORDER BY paymentDate DESC")
    fun getSubscriptionsBetweenDates(startDate: Long, endDate: Long): Flow<List<SubscriptionEntity>>

    @Query("SELECT COUNT(*) FROM subscriptions WHERE isPaid = 1")
    fun getTotalSubscriptionsCount(): Int

    @Query("SELECT * FROM subscriptions ORDER BY paymentDate DESC LIMIT 10")
    fun getRecentSubscriptions(): Flow<List<SubscriptionEntity>>
}
