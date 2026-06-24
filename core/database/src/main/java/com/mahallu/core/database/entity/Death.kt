package com.mahallu.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deaths")
data class Death(
    @PrimaryKey val id: Long = 0,
    val name: String,
    val fatherName: String,
    val dateOfDeath: Long,
    val burialDate: Long?,
    val causeOfDeath: String?,
    val remarks: String?,
    val createdAt: Long = System.currentTimeMillis()
)
