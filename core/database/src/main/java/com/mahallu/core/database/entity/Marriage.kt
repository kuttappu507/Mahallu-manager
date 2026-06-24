package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "marriages")
data class Marriage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brideName: String,
    val brideFatherName: String,
    val groomName: String,
    val groomFatherName: String,
    val witness1Name: String,
    val witness2Name: String,
    val maharAmount: Double,
    val nikahDate: Date,
    val registrationNumber: String,
    val brideFamilyId: Long?,
    val groomFamilyId: Long?,
    val remarks: String?,
    val createdAt: Date = Date()
)
