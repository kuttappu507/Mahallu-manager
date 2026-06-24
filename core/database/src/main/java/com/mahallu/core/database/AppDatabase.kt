package com.mahallu.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mahallu.core.database.dao.*
import com.mahallu.core.database.entity.*

@Database(
    entities = [
        User::class,
        Family::class,
        Member::class,
        Subscription::class,
        Donation::class,
        Income::class,
        Expense::class,
        Marriage::class,
        Death::class,
        WelfareRequest::class,
        Settings::class,
        BackupLog::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun familyDao(): FamilyDao
    abstract fun memberDao(): MemberDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun donationDao(): DonationDao
    abstract fun incomeDao(): IncomeDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun marriageDao(): MarriageDao
    abstract fun deathDao(): DeathDao
    abstract fun welfareDao(): WelfareDao
    abstract fun settingsDao(): SettingsDao
    abstract fun backupDao(): BackupDao
    
    companion object {
        const val DATABASE_NAME = "mahallu_db"
    }
}
