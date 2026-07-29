package com.mahallu.manager.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
import com.mahallu.manager.core.database.entity.AuditLogEntity
import com.mahallu.manager.core.database.entity.BackupEntity
import com.mahallu.manager.core.database.entity.CertificateEntity
import com.mahallu.manager.core.database.entity.DeathEntity
import com.mahallu.manager.core.database.entity.DonationEntity
import com.mahallu.manager.core.database.entity.FamilyEntity
import com.mahallu.manager.core.database.entity.FinanceEntryEntity
import com.mahallu.manager.core.database.entity.MarriageEntity
import com.mahallu.manager.core.database.entity.MemberEntity
import com.mahallu.manager.core.database.entity.NotificationEntity
import com.mahallu.manager.core.database.entity.SettingsEntity
import com.mahallu.manager.core.database.entity.SubscriptionEntity
import com.mahallu.manager.core.database.entity.UserEntity
import com.mahallu.manager.core.database.entity.WelfareEntity

class MahalluTypeConverters {
    @TypeConverter fun longToBoolean(v: Long?): Boolean? = v?.let { it != 0L }
    @TypeConverter fun booleanToLong(v: Boolean?): Long? = v?.let { if (it) 1L else 0L }
}

@Database(
    entities = [
        UserEntity::class,
        FamilyEntity::class,
        MemberEntity::class,
        SubscriptionEntity::class,
        DonationEntity::class,
        FinanceEntryEntity::class,
        MarriageEntity::class,
        DeathEntity::class,
        WelfareEntity::class,
        CertificateEntity::class,
        AuditLogEntity::class,
        SettingsEntity::class,
        BackupEntity::class,
        NotificationEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(MahalluTypeConverters::class)
abstract class MahalluDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun familyDao(): FamilyDao
    abstract fun memberDao(): MemberDao
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun donationDao(): DonationDao
    abstract fun financeDao(): FinanceDao
    abstract fun marriageDao(): MarriageDao
    abstract fun deathDao(): DeathDao
    abstract fun welfareDao(): WelfareDao
    abstract fun certificateDao(): CertificateDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun settingsDao(): SettingsDao
    abstract fun backupDao(): BackupDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        const val NAME = "mahallu_db"

        // Sample migrations from version 1 -> 2 example. Replace with real schema diffs when needed.
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE families ADD COLUMN notes TEXT")
            }
        }
    }
}