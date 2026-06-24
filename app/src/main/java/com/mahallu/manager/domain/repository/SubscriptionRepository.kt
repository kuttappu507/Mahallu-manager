package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getAllSubscriptions(): Flow<List<Subscription>>
    fun getSubscriptionById(id: Int): Flow<Subscription?>
    suspend fun insertSubscription(subscription: Subscription)
    suspend fun updateSubscription(subscription: Subscription)
    suspend fun deleteSubscription(subscription: Subscription)
    fun getSubscriptionsByFamilyId(familyId: Int): Flow<List<Subscription>>
    fun getSubscriptionsByMonth(year: Int, month: Int): Flow<List<Subscription>>
    fun searchSubscriptions(query: String): Flow<List<Subscription>>
    suspend fun getTotalCollection(): Double
    suspend fun getPendingDues(): Double
}
