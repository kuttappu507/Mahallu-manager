package com.mahallu.manager.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mahallu.manager.data.local.AppDatabase
import com.mahallu.manager.data.local.dao.*
import com.mahallu.manager.core.security.SecurityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    @Singleton
    fun provideSecurityManager(): SecurityManager {
        return SecurityManager()
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}
