package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marriages")
data class MarriageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val registrationNumber: String,
    val brideName: String,
    val brideFatherName: String,
    val groomName: String,
    val groomFatherName: String,
    val witness1Name: String,
    val witness2Name: String,
    val maharAmount: Double,
    val nikahDate: Long,
    val remarks: String?,
    val certificateGenerated: Boolean = false,
    val recordedBy: Long,
    val createdAt: Long = System.currentTimeMillis()
)
