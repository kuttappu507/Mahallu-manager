package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "deaths",
    foreignKeys = [ForeignKey(entity = Member::class, parentColumns = ["id"], childColumns = ["memberId"])]
)
data class Death(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val memberId: Long?,
    val name: String,
    val fatherName: String,
    val dateOfDeath: Date,
    val burialDate: Date?,
    val causeOfDeath: String?,
    val age: Int?,
    val remarks: String?,
    val familyId: Long?,
    val createdAt: Date = Date()
)
