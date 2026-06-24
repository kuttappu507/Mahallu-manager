package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "welfare",
    foreignKeys = [
        ForeignKey(entity = Family::class, parentColumns = ["id"], childColumns = ["familyId"]),
        ForeignKey(entity = Member::class, parentColumns = ["id"], childColumns = ["memberId"])
    ],
    indices = [Index(value = ["familyId"]), Index(value = ["memberId"])]
)
data class WelfareRequest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyId: Long?,
    val memberId: Long?,
    val applicantName: String,
    val type: WelfareType,
    val amount: Double,
    val reason: String,
    val status: WelfareStatus = WelfareStatus.PENDING,
    val requestedDate: Date = Date(),
    val approvedDate: Date?,
    val disbursedDate: Date?,
    val approvedBy: Long?,
    val remarks: String?,
    val createdAt: Date = Date()
)

enum class WelfareType { MEDICAL_AID, EDUCATION_AID, MARRIAGE_ASSISTANCE, FINANCIAL_ASSISTANCE, OTHER }
enum class WelfareStatus { PENDING, APPROVED, REJECTED, DISBURSED }
