package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "welfare_requests",
    indices = [Index(value = ["familyId"])],
    foreignKeys = [
        ForeignKey(
            entity = FamilyEntity::class,
            parentColumns = ["id"],
            childColumns = ["familyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class WelfareRequestEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val familyId: Long?,
    val applicantName: String,
    val category: WelfareCategory,
    val amount: Double,
    val reason: String,
    val status: WelfareStatus = WelfareStatus.PENDING,
    val applicationDate: Long,
    val approvedBy: Long?,
    val approvedDate: Long?,
    val disbursedDate: Long?,
    val remarks: String?,
    val recordedBy: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class WelfareCategory {
    MEDICAL_AID,
    EDUCATION_AID,
    MARRIAGE_ASSISTANCE,
    FINANCIAL_ASSISTANCE,
    OTHER
}

enum class WelfareStatus {
    PENDING,
    APPROVED,
    REJECTED,
    DISBURSED
}
