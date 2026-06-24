package com.mahallu.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mahallu.core.database.dao.*
import com.mahallu.core.database.entity.*

@Database(
    version = 1,
    entities = [
        Family::class,
        Member::class,
        User::class,
        Subscription::class,
        Donation::class,
        Finance::class,
        Marriage::class,
        Death::class,
        WelfareRequest::class,
        Settings::class
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun familyDao(): FamilyDao
    abstract fun memberDao(): MemberDao
    abstract fun userDao(): UserDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun donationDao(): DonationDao
    abstract fun financeDao(): FinanceDao
    abstract fun marriageDao(): MarriageDao
    abstract fun deathDao(): DeathDao
    abstract fun welfareDao(): WelfareDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        const val DATABASE_NAME = "mahallu_database"
    }
}
