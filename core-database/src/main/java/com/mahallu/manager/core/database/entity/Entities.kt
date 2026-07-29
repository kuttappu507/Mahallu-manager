package com.mahallu.manager.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey val id: String,
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val role: String,
    val phone: String? = null,
    val email: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long? = null
)

@Entity(
    tableName = "families",
    indices = [
        Index(value = ["familyNumber"], unique = true),
        Index(value = ["houseName"]),
        Index(value = ["status"])
    ]
)
data class FamilyEntity(
    @PrimaryKey val id: String,
    val familyNumber: String,
    val houseName: String,
    val houseNumber: String? = null,
    val ward: String? = null,
    val area: String? = null,
    val address: String,
    val pincode: String? = null,
    val primaryMobile: String? = null,
    val secondaryMobile: String? = null,
    val email: String? = null,
    val photoUri: String? = null,
    val status: String = "ACTIVE", // ACTIVE, INACTIVE, ARCHIVED
    val notes: String? = null,
    val headMemberId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null
)

@Entity(
    tableName = "members",
    indices = [
        Index(value = ["memberNumber"], unique = true),
        Index(value = ["familyId"]),
        Index(value = ["name"]),
        Index(value = ["mobile"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = FamilyEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemberEntity(
    @PrimaryKey val id: String,
    val memberNumber: String,
    val familyId: String,
    val name: String,
    val arabicName: String? = null,
    val gender: String, // MALE, FEMALE, OTHER
    val dateOfBirth: Long,
    val occupation: String? = null,
    val education: String? = null,
    val bloodGroup: String? = null,
    val maritalStatus: String? = null, // SINGLE, MARRIED, DIVORCED, WIDOWED
    val mobile: String? = null,
    val email: String? = null,
    val nationality: String? = null,
    val address: String? = null,
    val emergencyContactName: String? = null,
    val emergencyContactNumber: String? = null,
    val relationToHead: String? = null, // HEAD, SPOUSE, SON, DAUGHTER, etc.
    val photoUri: String? = null,
    val isAlive: Boolean = true,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "subscriptions",
    indices = [
        Index(value = ["receiptNumber"], unique = true),
        Index(value = ["familyId"]),
        Index(value = ["memberId"]),
        Index(value = ["date"]),
        Index(value = ["type"])
    ],
    foreignKeys = [
        ForeignKey(entity = FamilyEntity::class, parentColumns = ["id"], childColumns = ["familyId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = MemberEntity::class, parentColumns = ["id"], childColumns = ["memberId"], onDelete = ForeignKey.SET_NULL)
    ]
)
data class SubscriptionEntity(
    @PrimaryKey val id: String,
    val receiptNumber: String,
    val familyId: String,
    val memberId: String? = null,
    val type: String, // MONTHLY, QUARTERLY, YEARLY, SPECIAL
    val amount: Double,
    val date: Long,
    val paymentMethod: String, // CASH, UPI, BANK, CHEQUE, OTHER
    val reference: String? = null,
    val remarks: String? = null,
    val receivedBy: String? = null,
    val year: Int,
    val month: Int,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "donations",
    indices = [
        Index(value = ["receiptNumber"], unique = true),
        Index(value = ["donorName"]),
        Index(value = ["date"]),
        Index(value = ["category"])
    ]
)
data class DonationEntity(
    @PrimaryKey val id: String,
    val receiptNumber: String,
    val donorName: String,
    val donorMobile: String? = null,
    val donorFamilyId: String? = null,
    val amount: Double,
    val category: String, // GENERAL, MASJID, BUILDING, EDUCATION, MEDICAL, WELFARE, OTHER
    val purpose: String? = null,
    val date: Long,
    val paymentMethod: String,
    val reference: String? = null,
    val remarks: String? = null,
    val receivedBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "finance_entries",
    indices = [
        Index(value = ["date"]),
        Index(value = ["type"]),
        Index(value = ["category"])
    ]
)
data class FinanceEntryEntity(
    @PrimaryKey val id: String,
    val type: String, // INCOME, EXPENSE
    val category: String, // SUBSCRIPTION, DONATION, RENT, OTHER_INCOME, SALARY, ELECTRICITY, WATER, MAINTENANCE, WELFARE, OTHER_EXPENSE
    val amount: Double,
    val date: Long,
    val description: String,
    val paymentMethod: String,
    val reference: String? = null,
    val receiptId: String? = null,
    val receivedBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "marriages",
    indices = [
        Index(value = ["registrationNumber"], unique = true),
        Index(value = ["brideName"]),
        Index(value = ["groomName"]),
        Index(value = ["nikahDate"])
    ]
)
data class MarriageEntity(
    @PrimaryKey val id: String,
    val registrationNumber: String,
    val brideName: String,
    val brideFatherName: String,
    val brideAge: Int? = null,
    val brideId: String? = null,
    val groomName: String,
    val groomFatherName: String,
    val groomAge: Int? = null,
    val groomId: String? = null,
    val witnessOneName: String,
    val witnessTwoName: String,
    val maharAmount: Double,
    val nikahDate: Long,
    val registrationDate: Long,
    val nikahLocation: String? = null,
    val remarks: String? = null,
    val performedBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "deaths",
    indices = [
        Index(value = ["registrationNumber"], unique = true),
        Index(value = ["name"]),
        Index(value = ["dateOfDeath"])
    ]
)
data class DeathEntity(
    @PrimaryKey val id: String,
    val registrationNumber: String,
    val name: String,
    val fatherName: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    val dateOfDeath: Long,
    val burialDate: Long? = null,
    val burialLocation: String? = null,
    val causeOfDeath: String? = null,
    val familyId: String? = null,
    val memberId: String? = null,
    val remarks: String? = null,
    val recordedBy: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "welfare_requests",
    indices = [
        Index(value = ["familyId"]),
        Index(value = ["status"]),
        Index(value = ["date"])
    ],
    foreignKeys = [
        ForeignKey(entity = FamilyEntity::class, parentColumns = ["id"], childColumns = ["familyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class WelfareEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val applicantName: String,
    val memberId: String? = null,
    val category: String, // MEDICAL, EDUCATION, MARRIAGE, FINANCIAL, OTHER
    val amount: Double,
    val reason: String,
    val status: String, // PENDING, APPROVED, REJECTED, DISBURSED
    val date: Long,
    val approvedBy: String? = null,
    val approvedDate: Long? = null,
    val disbursedDate: Long? = null,
    val remarks: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "certificates",
    indices = [
        Index(value = ["certificateNumber"], unique = true),
        Index(value = ["type"]),
        Index(value = ["issuedDate"])
    ]
)
data class CertificateEntity(
    @PrimaryKey val id: String,
    val certificateNumber: String,
    val type: String, // MEMBERSHIP, RESIDENCE, MARRIAGE, DEATH
    val subjectId: String,
    val subjectName: String,
    val issuedTo: String,
    val purpose: String? = null,
    val details: String? = null,
    val issuedDate: Long,
    val validUntil: Long? = null,
    val issuedBy: String? = null,
    val pdfPath: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "audit_logs",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["timestamp"]),
        Index(value = ["action"])
    ]
)
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val userName: String,
    val action: String,
    val entityType: String? = null,
    val entityId: String? = null,
    val description: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "settings"
)
data class SettingsEntity(
    @PrimaryKey val key: String,
    val value: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "backups",
    indices = [
        Index(value = ["createdAt"]),
        Index(value = ["status"])
    ]
)
data class BackupEntity(
    @PrimaryKey val id: String,
    val fileName: String,
    val localPath: String? = null,
    val driveFileId: String? = null,
    val driveLink: String? = null,
    val size: Long,
    val status: String, // IN_PROGRESS, SUCCESS, FAILED, RESTORED
    val type: String, // AUTO, MANUAL
    val checksum: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val message: String? = null
)

@Entity(
    tableName = "notifications",
    indices = [Index(value = ["timestamp"])]
)
data class NotificationEntity(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val type: String, // INFO, WARNING, REMINDER
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis(),
    val actionRoute: String? = null
)