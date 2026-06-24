package com.mahallu.core.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    @Singleton
    fun provideFamilyDao(database: AppDatabase) = database.familyDao()

    @Provides
    @Singleton
    fun provideMemberDao(database: AppDatabase) = database.memberDao()

    @Provides
    @Singleton
    fun provideSubscriptionDao(database: AppDatabase) = database.subscriptionDao()

    @Provides
    @Singleton
    fun provideDonationDao(database: AppDatabase) = database.donationDao()

    @Provides
    @Singleton
    fun provideIncomeDao(database: AppDatabase) = database.incomeDao()

    @Provides
    @Singleton
    fun provideExpenseDao(database: AppDatabase) = database.expenseDao()

    @Provides
    @Singleton
    fun provideMarriageDao(database: AppDatabase) = database.marriageDao()

    @Provides
    @Singleton
    fun provideDeathDao(database: AppDatabase) = database.deathDao()

    @Provides
    @Singleton
    fun provideWelfareDao(database: AppDatabase) = database.welfareDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase) = database.settingsDao()

    @Provides
    @Singleton
    fun provideBackupDao(database: AppDatabase) = database.backupDao()
}
