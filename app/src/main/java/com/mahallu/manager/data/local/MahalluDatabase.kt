package com.mahallu.manager.data.local

import androidx.room.*
import com.mahallu.manager.data.local.dao.*
import com.mahallu.manager.data.local.entity.*

@Database(
    version = 1,
    entities = [
        UserEntity::class,
        FamilyEntity::class,
        MemberEntity::class,
        SubscriptionEntity::class,
        DonationEntity::class,
        TransactionEntity::class,
        MarriageEntity::class,
        DeathEntity::class,
        WelfareRequestEntity::class,
        SettingsEntity::class
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MahalluDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun familyDao(): FamilyDao
    abstract fun memberDao(): MemberDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun donationDao(): DonationDao
    abstract fun transactionDao(): TransactionDao
    abstract fun marriageDao(): MarriageDao
    abstract fun deathDao(): DeathDao
    abstract fun welfareDao(): WelfareDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val DATABASE_NAME = "mahallu_database"
    }
}
