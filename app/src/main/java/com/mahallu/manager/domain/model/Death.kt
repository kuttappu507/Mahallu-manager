package com.mahallu.manager.domain.model

import java.util.Date

data class Death(
    val id: Int = 0,
    val name: String,
    val fatherName: String,
    val dateOfBirth: Date?,
    val dateOfDeath: Date,
    val burialDate: Date,
    val causeOfDeath: String?,
    val remarks: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
