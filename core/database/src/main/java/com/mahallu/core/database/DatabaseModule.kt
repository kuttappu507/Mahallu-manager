package com.mahallu.core.database

import android.content.Context
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
        return AppDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideFamilyDao(database: AppDatabase): FamilyDao {
        return database.familyDao()
    }

    @Provides
    @Singleton
    fun provideMemberDao(database: AppDatabase): MemberDao {
        return database.memberDao()
    }

    @Provides
    @Singleton
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideSubscriptionDao(database: AppDatabase): SubscriptionDao {
        return database.subscriptionDao()
    }

    @Provides
    @Singleton
    fun provideDonationDao(database: AppDatabase): DonationDao {
        return database.donationDao()
    }

    @Provides
    @Singleton
    fun provideFinanceDao(database: AppDatabase): FinanceDao {
        return database.financeDao()
    }

    @Provides
    @Singleton
    fun provideMarriageDao(database: AppDatabase): MarriageDao {
        return database.marriageDao()
    }

    @Provides
    @Singleton
    fun provideDeathDao(database: AppDatabase): DeathDao {
        return database.deathDao()
    }

    @Provides
    @Singleton
    fun provideWelfareDao(database: AppDatabase): WelfareDao {
        return database.welfareDao()
    }

    @Provides
    @Singleton
    fun provideSettingsDao(database: AppDatabase): SettingsDao {
        return database.settingsDao()
    }
}
