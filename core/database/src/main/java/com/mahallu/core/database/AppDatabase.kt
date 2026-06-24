package com.mahallu.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mahallu.core.database.dao.*
import com.mahallu.core.database.entity.*

@Database(
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
    version = 1,
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
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mahallu_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
