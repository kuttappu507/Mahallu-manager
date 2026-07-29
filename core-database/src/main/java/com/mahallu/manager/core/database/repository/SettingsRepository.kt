package com.mahallu.manager.core.database.repository

import com.mahallu.manager.core.database.dao.SettingsDao
import com.mahallu.manager.core.database.entity.SettingsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(private val dao: SettingsDao) {
    fun observeAll(): Flow<List<SettingsEntity>> = dao.observeAll()

    suspend fun getString(key: String, default: String = ""): String =
        dao.get(key)?.value ?: default

    suspend fun getInt(key: String, default: Int = 0): Int =
        dao.get(key)?.value?.toIntOrNull() ?: default

    suspend fun getLong(key: String, default: Long = 0L): Long =
        dao.get(key)?.value?.toLongOrNull() ?: default

    suspend fun getBoolean(key: String, default: Boolean = false): Boolean =
        dao.get(key)?.value?.toBooleanStrictOrNull() ?: default

    suspend fun put(key: String, value: String) =
        dao.upsert(SettingsEntity(key, value))

    suspend fun putBool(key: String, value: Boolean) =
        dao.upsert(SettingsEntity(key, value.toString()))

    suspend fun putInt(key: String, value: Int) =
        dao.upsert(SettingsEntity(key, value.toString()))

    suspend fun putLong(key: String, value: Long) =
        dao.upsert(SettingsEntity(key, value.toString()))
}