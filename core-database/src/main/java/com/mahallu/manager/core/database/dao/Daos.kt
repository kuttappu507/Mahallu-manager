package com.mahallu.manager.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mahallu.manager.core.database.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY fullName")
    fun observeAll(): Flow<List<UserEntity>>

    @Query("UPDATE users SET lastLoginAt = :time WHERE id = :id")
    suspend fun updateLastLogin(id: String, time: Long)

    @Query("DELETE FROM users")
    suspend fun clear()
}

@Dao
interface FamilyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(family: com.mahallu.manager.core.database.entity.FamilyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(families: List<com.mahallu.manager.core.database.entity.FamilyEntity>)

    @Update
    suspend fun update(family: com.mahallu.manager.core.database.entity.FamilyEntity)

    @Query("DELETE FROM families WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM families WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.FamilyEntity?

    @Query("SELECT * FROM families WHERE id = :id")
    fun observeById(id: String): Flow<com.mahallu.manager.core.database.entity.FamilyEntity?>

    @Query("SELECT * FROM families ORDER BY familyNumber DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.FamilyEntity>>

    @Query("SELECT * FROM families ORDER BY familyNumber DESC LIMIT :limit OFFSET :offset")
    suspend fun page(limit: Int, offset: Int): List<com.mahallu.manager.core.database.entity.FamilyEntity>

    @Query("""
        SELECT * FROM families
        WHERE familyNumber LIKE '%' || :q || '%'
           OR houseName LIKE '%' || :q || '%'
           OR primaryMobile LIKE '%' || :q || '%'
           OR address LIKE '%' || :q || '%'
        ORDER BY familyNumber DESC
    """)
    suspend fun search(q: String): List<com.mahallu.manager.core.database.entity.FamilyEntity>

    @Query("SELECT COUNT(*) FROM families")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM families")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM families WHERE status = :status")
    suspend fun countByStatus(status: String): Int

    @Query("SELECT COUNT(*) FROM families WHERE status = :status")
    fun observeCountByStatus(status: String): Flow<Int>

    @Query("DELETE FROM families")
    suspend fun clear()
}

@Dao
interface MemberDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(member: com.mahallu.manager.core.database.entity.MemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(members: List<com.mahallu.manager.core.database.entity.MemberEntity>)

    @Update
    suspend fun update(member: com.mahallu.manager.core.database.entity.MemberEntity)

    @Query("DELETE FROM members WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM members WHERE familyId = :familyId")
    suspend fun deleteByFamily(familyId: String)

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.MemberEntity?

    @Query("SELECT * FROM members WHERE id = :id")
    fun observeById(id: String): Flow<com.mahallu.manager.core.database.entity.MemberEntity?>

    @Query("SELECT * FROM members WHERE familyId = :familyId ORDER BY dateOfBirth")
    fun observeByFamily(familyId: String): Flow<List<com.mahallu.manager.core.database.entity.MemberEntity>>

    @Query("SELECT * FROM members ORDER BY name")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.MemberEntity>>

    @Query("""
        SELECT * FROM members
        WHERE name LIKE '%' || :q || '%'
           OR mobile LIKE '%' || :q || '%'
           OR memberNumber LIKE '%' || :q || '%'
        ORDER BY name
    """)
    suspend fun search(q: String): List<com.mahallu.manager.core.database.entity.MemberEntity>

    @Query("SELECT COUNT(*) FROM members")
    fun observeCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM members")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM members WHERE familyId = :familyId")
    suspend fun countByFamily(familyId: String): Int

    @Query("DELETE FROM members")
    suspend fun clear()
}

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.SubscriptionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.SubscriptionEntity>)

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.SubscriptionEntity?

    @Query("SELECT * FROM subscriptions ORDER BY date DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE familyId = :familyId ORDER BY date DESC")
    fun observeByFamily(familyId: String): Flow<List<com.mahallu.manager.core.database.entity.SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeByDateRange(start: Long, end: Long): Flow<List<com.mahallu.manager.core.database.entity.SubscriptionEntity>>

    @Query("SELECT SUM(amount) FROM subscriptions WHERE date BETWEEN :start AND :end")
    fun observeTotalBetween(start: Long, end: Long): Flow<Double?>

    @Query("SELECT DISTINCT familyId FROM subscriptions WHERE type = 'MONTHLY' AND date BETWEEN :start AND :end")
    fun observePaidFamilyIds(start: Long, end: Long): Flow<List<String>>

    @Query("SELECT * FROM subscriptions ORDER BY date DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<com.mahallu.manager.core.database.entity.SubscriptionEntity>

    @Query("SELECT * FROM subscriptions ORDER BY date DESC")
    suspend fun all(): List<com.mahallu.manager.core.database.entity.SubscriptionEntity>

    @Query("SELECT DISTINCT year FROM subscriptions ORDER BY year DESC")
    fun observeYears(): Flow<List<Int>>

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM subscriptions")
    suspend fun clear()
}

@Dao
interface DonationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.DonationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.DonationEntity>)

    @Query("SELECT * FROM donations WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.DonationEntity?

    @Query("SELECT * FROM donations ORDER BY date DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.DonationEntity>>

    @Query("SELECT * FROM donations WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeByRange(start: Long, end: Long): Flow<List<com.mahallu.manager.core.database.entity.DonationEntity>>

    @Query("SELECT SUM(amount) FROM donations WHERE date BETWEEN :start AND :end")
    fun observeTotalBetween(start: Long, end: Long): Flow<Double?>

    @Query("SELECT * FROM donations ORDER BY date DESC LIMIT :limit")
    suspend fun recent(limit: Int): List<com.mahallu.manager.core.database.entity.DonationEntity>

    @Query("SELECT * FROM donations ORDER BY date DESC")
    suspend fun all(): List<com.mahallu.manager.core.database.entity.DonationEntity>

    @Query("DELETE FROM donations WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM donations")
    suspend fun clear()
}

@Dao
interface FinanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.FinanceEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.FinanceEntryEntity>)

    @Query("SELECT * FROM finance_entries WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.FinanceEntryEntity?

    @Query("SELECT * FROM finance_entries ORDER BY date DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.FinanceEntryEntity>>

    @Query("SELECT * FROM finance_entries WHERE type = :type ORDER BY date DESC")
    fun observeByType(type: String): Flow<List<com.mahallu.manager.core.database.entity.FinanceEntryEntity>>

    @Query("SELECT * FROM finance_entries WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun observeByRange(start: Long, end: Long): Flow<List<com.mahallu.manager.core.database.entity.FinanceEntryEntity>>

    @Query("SELECT SUM(amount) FROM finance_entries WHERE type = 'INCOME' AND date BETWEEN :start AND :end")
    fun observeTotalIncome(start: Long, end: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM finance_entries WHERE type = 'EXPENSE' AND date BETWEEN :start AND :end")
    fun observeTotalExpense(start: Long, end: Long): Flow<Double?>

    @Query("SELECT * FROM finance_entries ORDER BY date DESC")
    suspend fun all(): List<com.mahallu.manager.core.database.entity.FinanceEntryEntity>

    @Query("DELETE FROM finance_entries WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM finance_entries")
    suspend fun clear()
}

@Dao
interface MarriageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.MarriageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.MarriageEntity>)

    @Query("SELECT * FROM marriages WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.MarriageEntity?

    @Query("SELECT * FROM marriages WHERE id = :id")
    fun observeById(id: String): Flow<com.mahallu.manager.core.database.entity.MarriageEntity?>

    @Query("SELECT * FROM marriages ORDER BY nikahDate DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.MarriageEntity>>

    @Query("""
        SELECT * FROM marriages
        WHERE brideName LIKE '%' || :q || '%'
           OR groomName LIKE '%' || :q || '%'
           OR registrationNumber LIKE '%' || :q || '%'
        ORDER BY nikahDate DESC
    """)
    suspend fun search(q: String): List<com.mahallu.manager.core.database.entity.MarriageEntity>

    @Query("SELECT * FROM marriages ORDER BY nikahDate DESC")
    suspend fun all(): List<com.mahallu.manager.core.database.entity.MarriageEntity>

    @Query("DELETE FROM marriages WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM marriages")
    suspend fun clear()
}

@Dao
interface DeathDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.DeathEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.DeathEntity>)

    @Query("SELECT * FROM deaths WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.DeathEntity?

    @Query("SELECT * FROM deaths WHERE id = :id")
    fun observeById(id: String): Flow<com.mahallu.manager.core.database.entity.DeathEntity?>

    @Query("SELECT * FROM deaths ORDER BY dateOfDeath DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.DeathEntity>>

    @Query("""
        SELECT * FROM deaths
        WHERE name LIKE '%' || :q || '%'
           OR fatherName LIKE '%' || :q || '%'
        ORDER BY dateOfDeath DESC
    """)
    suspend fun search(q: String): List<com.mahallu.manager.core.database.entity.DeathEntity>

    @Query("SELECT * FROM deaths ORDER BY dateOfDeath DESC")
    suspend fun all(): List<com.mahallu.manager.core.database.entity.DeathEntity>

    @Query("DELETE FROM deaths WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM deaths")
    suspend fun clear()
}

@Dao
interface WelfareDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.WelfareEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.WelfareEntity>)

    @Query("SELECT * FROM welfare_requests WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.WelfareEntity?

    @Query("SELECT * FROM welfare_requests WHERE id = :id")
    fun observeById(id: String): Flow<com.mahallu.manager.core.database.entity.WelfareEntity?>

    @Query("SELECT * FROM welfare_requests ORDER BY date DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.WelfareEntity>>

    @Query("SELECT * FROM welfare_requests WHERE status = :status ORDER BY date DESC")
    fun observeByStatus(status: String): Flow<List<com.mahallu.manager.core.database.entity.WelfareEntity>>

    @Query("""
        SELECT * FROM welfare_requests
        WHERE applicantName LIKE '%' || :q || '%'
           OR category LIKE '%' || :q || '%'
           OR reason LIKE '%' || :q || '%'
        ORDER BY date DESC
    """)
    suspend fun search(q: String): List<com.mahallu.manager.core.database.entity.WelfareEntity>

    @Query("SELECT SUM(amount) FROM welfare_requests WHERE status IN ('APPROVED','DISBURSED') AND date BETWEEN :start AND :end")
    fun observeDisbursedBetween(start: Long, end: Long): Flow<Double?>

    @Query("DELETE FROM welfare_requests WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM welfare_requests")
    suspend fun clear()
}

@Dao
interface CertificateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.CertificateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.CertificateEntity>)

    @Query("SELECT * FROM certificates WHERE id = :id")
    suspend fun getById(id: String): com.mahallu.manager.core.database.entity.CertificateEntity?

    @Query("SELECT * FROM certificates ORDER BY issuedDate DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.CertificateEntity>>

    @Query("SELECT COUNT(*) FROM certificates")
    fun observeCount(): Flow<Int>

    @Query("SELECT * FROM certificates WHERE type = :type ORDER BY issuedDate DESC")
    fun observeByType(type: String): Flow<List<com.mahallu.manager.core.database.entity.CertificateEntity>>

    @Query("DELETE FROM certificates")
    suspend fun clear()
}

@Dao
interface AuditLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: com.mahallu.manager.core.database.entity.AuditLogEntity)

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 100): Flow<List<com.mahallu.manager.core.database.entity.AuditLogEntity>>

    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun recent(limit: Int = 100): List<com.mahallu.manager.core.database.entity.AuditLogEntity>

    @Query("DELETE FROM audit_logs")
    suspend fun clear()
}

@Dao
interface SettingsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(setting: com.mahallu.manager.core.database.entity.SettingsEntity)

    @Query("SELECT * FROM settings WHERE key = :key LIMIT 1")
    suspend fun get(key: String): com.mahallu.manager.core.database.entity.SettingsEntity?

    @Query("SELECT * FROM settings")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.SettingsEntity>>

    @Query("DELETE FROM settings")
    suspend fun clear()
}

@Dao
interface BackupDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: com.mahallu.manager.core.database.entity.BackupEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<com.mahallu.manager.core.database.entity.BackupEntity>)

    @Query("SELECT * FROM backups ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<com.mahallu.manager.core.database.entity.BackupEntity>>

    @Query("SELECT * FROM backups ORDER BY createdAt DESC LIMIT 1")
    suspend fun latest(): com.mahallu.manager.core.database.entity.BackupEntity?

    @Query("SELECT * FROM backups WHERE status = 'SUCCESS' ORDER BY createdAt DESC")
    suspend fun allSuccessful(): List<com.mahallu.manager.core.database.entity.BackupEntity>

    @Query("DELETE FROM backups WHERE id = :id")
    suspend fun delete(id: String)

    @Query("DELETE FROM backups")
    suspend fun clear()
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: com.mahallu.manager.core.database.entity.NotificationEntity)

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int = 50): Flow<List<com.mahallu.manager.core.database.entity.NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllRead()

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun observeUnreadCount(): Flow<Int>

    @Query("DELETE FROM notifications")
    suspend fun clear()
}