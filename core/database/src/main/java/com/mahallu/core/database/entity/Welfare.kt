package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "welfare",
    foreignKeys = [
        ForeignKey(entity = Family::class, parentColumns = ["id"], childColumns = ["familyId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Member::class, parentColumns = ["id"], childColumns = ["memberId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("familyId"), Index("memberId")]
)
data class WelfareRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long?,
    val familyId: Long,
    val applicantName: String,
    val category: WelfareCategory,
    val amount: Double,
    val reason: String,
    val status: WelfareStatus = WelfareStatus.PENDING,
    val approvedBy: Long?,
    val approvedAt: Date?,
    val disbursedAt: Date?,
    val remarks: String?,
    val createdAt: Date = Date()
)

enum class WelfareCategory {
    MEDICAL_AID, EDUCATION_AID, MARRIAGE_ASSISTANCE, FINANCIAL_ASSISTANCE, OTHER
}

enum class WelfareStatus {
    PENDING, APPROVED, REJECTED, DISBURSED, CANCELLED
}
