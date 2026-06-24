package com.mahallu.core.database

import android.content.Context
import androidx.room.Room
import com.mahallu.core.database.dao.*
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
    fun provideFamilyDao(database: AppDatabase): FamilyDao = database.familyDao()

    @Provides
    @Singleton
    fun provideMemberDao(database: AppDatabase): MemberDao = database.memberDao()

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    @Singleton
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao = database.subscriptionDao()

    @Provides
    @Singleton
    fun provideDonationDao(database: AppDatabase): DonationDao = database.donationDao()

    @Provides
    @Singleton
    fun provideFinanceDao(database: AppDatabase): FinanceDao = database.financeDao()

    @Provides
    @Singleton
    fun provideMarriageDao(database: AppDatabase): MarriageDao = database.marriageDao()

    @Provides
    @Singleton
    fun provideDeathDao(database: AppDatabase): DeathDao = database.deathDao()

    @Provides
    @Singleton
    fun provideWelfareDao(database: AppDatabase): WelfareDao = database.welfareDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase): SettingsDao = database.settingsDao()
}
