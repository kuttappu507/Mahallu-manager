package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Subscription
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Query("SELECT * FROM subscriptions ORDER BY date DESC")
    fun getAllSubscriptions(): Flow<List<Subscription>>
    
    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getSubscriptionById(id: Long): Subscription?
    
    @Query("SELECT * FROM subscriptions WHERE familyId = :familyId ORDER BY date DESC")
    fun getSubscriptionsByFamily(familyId: Long): Flow<List<Subscription>>
    
    @Query("SELECT * FROM subscriptions WHERE memberId = :memberId ORDER BY date DESC")
    fun getSubscriptionsByMember(memberId: Long): Flow<List<Subscription>>
    
    @Query("SELECT * FROM subscriptions WHERE year = :year AND month = :month")
    fun getSubscriptionsByMonth(year: Int, month: Int): Flow<List<Subscription>>
    
    @Query("SELECT * FROM subscriptions WHERE year = :year")
    fun getSubscriptionsByYear(year: Int): Flow<List<Subscription>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subscription: Subscription): Long
    
    @Update
    suspend fun update(subscription: Subscription)
    
    @Delete
    suspend fun delete(subscription: Subscription)
    
    @Query("SELECT SUM(amount) FROM subscriptions WHERE year = :year AND month = :month")
    fun getTotalCollectionByMonth(year: Int, month: Int): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM subscriptions WHERE year = :year")
    fun getTotalCollectionByYear(year: Int): Flow<Double?>
}
