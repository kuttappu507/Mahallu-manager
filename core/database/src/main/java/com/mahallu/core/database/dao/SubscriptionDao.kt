package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.PaymentStatus
import com.mahallu.core.database.entity.Subscription
import com.mahallu.core.database.entity.SubscriptionType
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY date DESC")
    fun getAllSubscriptions(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE familyId = :familyId ORDER BY date DESC")
    fun getSubscriptionsByFamily(familyId: Long): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE memberId = :memberId ORDER BY date DESC")
    fun getSubscriptionsByMember(memberId: Long): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): Subscription?

    @Query("SELECT * FROM subscriptions WHERE status = :status ORDER BY date DESC")
    fun getSubscriptionsByStatus(status: PaymentStatus): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE subscriptionType = :type ORDER BY date DESC")
    fun getSubscriptionsByType(type: SubscriptionType): Flow<List<Subscription>>

    @Query("SELECT SUM(amount) FROM subscriptions WHERE status = 'PAID' AND date BETWEEN :startDate AND :endDate")
    suspend fun getTotalCollection(startDate: Long, endDate: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Delete
    suspend fun deleteSubscription(subscription: Subscription)

    @Query("UPDATE subscriptions SET status = :status WHERE id = :id")
    suspend fun updateSubscriptionStatus(id: Long, status: PaymentStatus)
}
