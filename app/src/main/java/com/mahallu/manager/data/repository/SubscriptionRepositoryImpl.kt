package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.SubscriptionDao
import com.mahallu.manager.data.local.entity.SubscriptionEntity
import com.mahallu.manager.domain.model.Subscription
import com.mahallu.manager.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) : SubscriptionRepository {

    override fun getAllSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.getAllSubscriptions().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSubscriptionById(id: Int): Flow<Subscription?> {
        return subscriptionDao.getSubscriptionById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertSubscription(subscription: Subscription) {
        subscriptionDao.insertSubscription(subscription.toEntity())
    }

    override suspend fun updateSubscription(subscription: Subscription) {
        subscriptionDao.updateSubscription(subscription.toEntity())
    }

    override suspend fun deleteSubscription(subscription: Subscription) {
        subscriptionDao.deleteSubscription(subscription.toEntity())
    }

    override fun getSubscriptionsByFamilyId(familyId: Int): Flow<List<Subscription>> {
        return subscriptionDao.getSubscriptionsByFamilyId(familyId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getSubscriptionsByMonth(year: Int, month: Int): Flow<List<Subscription>> {
        return subscriptionDao.getSubscriptionsByMonth(year, month).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchSubscriptions(query: String): Flow<List<Subscription>> {
        return subscriptionDao.searchSubscriptions("%$query%").map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun getTotalCollection(): Double {
        return subscriptionDao.getTotalCollection()
    }

    override suspend fun getPendingDues(): Double {
        return subscriptionDao.getPendingDues()
    }
}
