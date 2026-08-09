package com.mahallu.manager.core.database.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mahallu.manager.core.common.Constants
import com.mahallu.manager.core.database.MahalluDatabase
import com.mahallu.manager.core.database.dao.AuditLogDao
import com.mahallu.manager.core.database.dao.BackupDao
import com.mahallu.manager.core.database.dao.CertificateDao
import com.mahallu.manager.core.database.dao.DeathDao
import com.mahallu.manager.core.database.dao.DonationDao
import com.mahallu.manager.core.database.dao.FamilyDao
import com.mahallu.manager.core.database.dao.FinanceDao
import com.mahallu.manager.core.database.dao.MarriageDao
import com.mahallu.manager.core.database.dao.MemberDao
import com.mahallu.manager.core.database.dao.NotificationDao
import com.mahallu.manager.core.database.dao.SettingsDao
import com.mahallu.manager.core.database.dao.SubscriptionDao
import com.mahallu.manager.core.database.dao.UserDao
import com.mahallu.manager.core.database.dao.WelfareDao
import com.mahallu.manager.core.database.repository.LanguageController
import com.mahallu.manager.core.database.repository.SeedData
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
    fun provideDatabase(@ApplicationContext context: Context): MahalluDatabase {
        return Room.databaseBuilder(
            context,
            MahalluDatabase::class.java,
            Constants.DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed handled by SeedData on first launch
                }
            })
            // No destructive migrations: explicit migrations required.
            .build()
    }

    @Provides
    @Singleton
    fun provideAppLanguagePrefs(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(LanguageController.PREFS_FILE, Context.MODE_PRIVATE)

    @Provides fun provideUserDao(db: MahalluDatabase): UserDao = db.userDao()
    @Provides fun provideFamilyDao(db: MahalluDatabase): FamilyDao = db.familyDao()
    @Provides fun provideMemberDao(db: MahalluDatabase): MemberDao = db.memberDao()
    @Provides fun provideSubscriptionDao(db: MahalluDatabase): SubscriptionDao = db.subscriptionDao()
    @Provides fun provideDonationDao(db: MahalluDatabase): DonationDao = db.donationDao()
    @Provides fun provideFinanceDao(db: MahalluDatabase): FinanceDao = db.financeDao()
    @Provides fun provideMarriageDao(db: MahalluDatabase): MarriageDao = db.marriageDao()
    @Provides fun provideDeathDao(db: MahalluDatabase): DeathDao = db.deathDao()
    @Provides fun provideWelfareDao(db: MahalluDatabase): WelfareDao = db.welfareDao()
    @Provides fun provideCertificateDao(db: MahalluDatabase): CertificateDao = db.certificateDao()
    @Provides fun provideAuditLogDao(db: MahalluDatabase): AuditLogDao = db.auditLogDao()
    @Provides fun provideSettingsDao(db: MahalluDatabase): SettingsDao = db.settingsDao()
    @Provides fun provideBackupDao(db: MahalluDatabase): BackupDao = db.backupDao()
    @Provides fun provideNotificationDao(db: MahalluDatabase): NotificationDao = db.notificationDao()
}