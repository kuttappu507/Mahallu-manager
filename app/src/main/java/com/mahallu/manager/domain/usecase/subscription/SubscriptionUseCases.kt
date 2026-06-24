package com.mahallu.manager.domain.usecase.subscription

import com.mahallu.manager.domain.model.Subscription
import com.mahallu.manager.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllSubscriptionsUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<List<Subscription>> {
        return repository.getAllSubscriptions()
    }
}

class GetSubscriptionByIdUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(id: Int): Subscription? {
        return repository.getSubscriptionById(id)
    }
}

class SaveSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription) {
        if (subscription.id == 0) {
            repository.insertSubscription(subscription)
        } else {
            repository.updateSubscription(subscription)
        }
    }
}

class DeleteSubscriptionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(subscription: Subscription) {
        repository.deleteSubscription(subscription)
    }
}

class SearchSubscriptionsUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(query: String): Flow<List<Subscription>> {
        return repository.searchSubscriptions(query)
    }
}

class GetSubscriptionsByFamilyIdUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(familyId: Int): Flow<List<Subscription>> {
        return repository.getSubscriptionsByFamilyId(familyId)
    }
}

class GetPendingSubscriptionsUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    operator fun invoke(): Flow<List<Subscription>> {
        return repository.getPendingSubscriptions()
    }
}

class GetMonthlyCollectionUseCase @Inject constructor(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(year: Int, month: Int): Double {
        return repository.getMonthlyCollection(year, month)
    }
}
