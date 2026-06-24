package com.mahallu.manager.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deaths")
data class DeathEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val registrationNumber: String,
    val deceasedName: String,
    val fatherName: String,
    val dateOfBirth: Long?,
    val dateOfDeath: Long,
    val burialDate: Long?,
    val causeOfDeath: String?,
    val age: Int?,
    val remarks: String?,
    val certificateGenerated: Boolean = false,
    val recordedBy: Long,
    val createdAt: Long = System.currentTimeMillis()
)
