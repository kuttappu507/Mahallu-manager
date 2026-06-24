package com.mahallu.manager.domain.model

import java.util.Date

data class Marriage(
    val id: Int = 0,
    val brideName: String,
    val brideFather: String,
    val groomName: String,
    val groomFather: String,
    val witness1: String,
    val witness2: String,
    val mahar: Double,
    val nikahDate: Date,
    val registrationNumber: String,
    val remarks: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
