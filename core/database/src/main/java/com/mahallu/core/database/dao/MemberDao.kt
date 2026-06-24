package com.mahallu.core.database.dao

import androidx.room.*
import androidx.paging.PagingSource
import com.mahallu.core.database.entity.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveMembers(): Flow<List<Member>>
    
    @Query("SELECT * FROM members WHERE isActive = 1 ORDER BY name ASC")
    fun getPagingSource(): PagingSource<Int, Member>
    
    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): Member?
    
    @Query("SELECT * FROM members WHERE id = :id")
    fun getMemberByIdFlow(id: Long): Flow<Member?>
    
    @Query("SELECT * FROM members WHERE familyId = :familyId ORDER BY name ASC")
    fun getMembersByFamily(familyId: Long): Flow<List<Member>>
    
    @Query("SELECT * FROM members WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    fun searchMembers(query: String): Flow<List<Member>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: Member): Long
    
    @Update
    suspend fun update(member: Member)
    
    @Delete
    suspend fun delete(member: Member)
    
    @Query("UPDATE members SET isActive = 0, updatedAt = :timestamp WHERE id = :id")
    suspend fun deactivateMember(id: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("SELECT COUNT(*) FROM members WHERE isActive = 1")
    fun getActiveMembersCount(): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM members WHERE familyId = :familyId AND isActive = 1")
    fun getMemberCountByFamily(familyId: Long): Flow<Int>
}
