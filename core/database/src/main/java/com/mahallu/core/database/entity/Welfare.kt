package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "welfare_requests",
    foreignKeys = [ForeignKey(entity = Family::class, parentColumns = ["id"], childColumns = ["familyId"])]
)
data class WelfareRequest(
    @PrimaryKey val id: Long = 0,
    val familyId: Long?,
    val applicantName: String,
    val category: WelfareCategory,
    val amount: Double,
    val reason: String,
    val approvalStatus: ApprovalStatus = ApprovalStatus.PENDING,
    val approvedBy: Long?,
    val disbursedDate: Long?,
    val remarks: String?,
    val createdAt: Long = System.currentTimeMillis()
)

enum class WelfareCategory { MEDICAL_AID, EDUCATION_AID, MARRIAGE_ASSISTANCE, FINANCIAL_ASSISTANCE }
enum class ApprovalStatus { PENDING, APPROVED, REJECTED, DISBURSED }
