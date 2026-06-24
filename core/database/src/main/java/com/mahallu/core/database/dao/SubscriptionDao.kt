package com.mahallu.core.database.dao

import androidx.room.*
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

    @Query("SELECT * FROM subscriptions WHERE year = :year AND month = :month")
    fun getSubscriptionsByMonth(year: Int, month: Int): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE year = :year")
    fun getSubscriptionsByYear(year: Int): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE type = :type ORDER BY date DESC")
    fun getSubscriptionsByType(type: SubscriptionType): Flow<List<Subscription>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: Subscription): Long

    @Update
    suspend fun updateSubscription(subscription: Subscription)

    @Delete
    suspend fun deleteSubscription(subscription: Subscription)

    @Query("SELECT SUM(amount) FROM subscriptions WHERE year = :year AND month = :month")
    suspend fun getTotalCollectionByMonth(year: Int, month: Int): Double?

    @Query("SELECT SUM(amount) FROM subscriptions WHERE year = :year")
    suspend fun getTotalCollectionByYear(year: Int): Double?

    @Query("SELECT COUNT(*) FROM subscriptions WHERE year = :year AND month = :month")
    suspend fun getCountByMonth(year: Int, month: Int): Int
}
