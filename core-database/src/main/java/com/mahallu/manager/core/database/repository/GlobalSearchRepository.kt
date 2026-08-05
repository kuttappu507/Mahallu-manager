package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.DeathDao
import com.mahallu.manager.core.database.dao.DonationDao
import com.mahallu.manager.core.database.dao.FamilyDao
import com.mahallu.manager.core.database.dao.FinanceDao
import com.mahallu.manager.core.database.dao.MarriageDao
import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.dao.SubscriptionDao
import com.mahallu.manager.core.database.dao.WelfareDao
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GlobalSearchRepository @Inject constructor(
    private val familyDao: FamilyDao,
    private val memberDao: MemberDao,
    private val subDao: SubscriptionDao,
    private val donationDao: DonationDao,
    private val marriageDao: MarriageDao,
    private val deathDao: DeathDao,
    private val welfareDao: WelfareDao,
    private val financeDao: FinanceDao
) {
    suspend fun search(query: String, limitPerCategory: Int = 10): GlobalSearchResult = coroutineScope {
        if (query.isBlank()) return@coroutineScope GlobalSearchResult()

        val fams = async { familyDao.search(query).take(limitPerCategory) }
        val mems = async { memberDao.search(query).take(limitPerCategory) }
        val subs = async {
            // Sub doesn't have a search method directly; filter all recent
            subDao.recent(2000).filter {
                it.receiptNumber.contains(query, ignoreCase = true) ||
                (it.remarks?.contains(query, ignoreCase = true) == true)
            }.take(limitPerCategory)
        }
        val dons = async {
            donationDao.recent(2000).filter {
                it.receiptNumber.contains(query, ignoreCase = true) ||
                it.donorName.contains(query, ignoreCase = true) ||
                (it.purpose?.contains(query, ignoreCase = true) == true)
            }.take(limitPerCategory)
        }
        val marrs = async { marriageDao.search(query).take(limitPerCategory) }
        val deats = async { deathDao.search(query).take(limitPerCategory) }
        val wels = async { welfareDao.search(query).take(limitPerCategory) }
        val fins = async {
            financeDao.all().filter {
                it.description.contains(query, ignoreCase = true) ||
                it.category.contains(query, ignoreCase = true)
            }.take(limitPerCategory)
        }

        GlobalSearchResult(
            families = fams.await(),
            members = mems.await(),
            subscriptions = subs.await(),
            donations = dons.await(),
            marriages = marrs.await(),
            deaths = deats.await(),
            welfare = wels.await(),
            finance = fins.await()
        )
    }
}