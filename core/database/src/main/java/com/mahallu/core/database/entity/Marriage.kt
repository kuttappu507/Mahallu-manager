package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marriages")
data class Marriage(
    @PrimaryKey val id: Long = 0,
    val brideName: String,
    val brideFatherName: String,
    val groomName: String,
    val groomFatherName: String,
    val witness1Name: String?,
    val witness2Name: String?,
    val maharAmount: Double?,
    val nikahDate: Long,
    val registrationNumber: String,
    val remarks: String?,
    val createdAt: Long = System.currentTimeMillis()
)
