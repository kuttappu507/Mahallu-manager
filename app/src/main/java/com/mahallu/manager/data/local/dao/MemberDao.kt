package com.mahallu.manager.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.mahallu.manager.data.local.entity.MemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {

    @Query("SELECT * FROM members WHERE isActive = 1 ORDER BY name ASC")
    fun getAllMembers(): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE isActive = 1 ORDER BY name ASC")
    fun getAllMembersPaging(): PagingSource<Int, MemberEntity>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): MemberEntity?

    @Query("SELECT * FROM members WHERE familyId = :familyId ORDER BY name ASC")
    fun getMembersByFamilyId(familyId: Long): Flow<List<MemberEntity>>

    @Query("SELECT * FROM members WHERE memberId = :memberId")
    suspend fun getMemberByMemberId(memberId: String): MemberEntity?

    @Query("SELECT * FROM members WHERE name LIKE '%' || :query || '%' OR mobile LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
    fun searchMembers(query: String): Flow<List<MemberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: MemberEntity): Long

    @Update
    suspend fun updateMember(member: MemberEntity)

    @Delete
    suspend fun deleteMember(member: MemberEntity)

    @Query("UPDATE members SET isActive = 0 WHERE id = :id")
    suspend fun deactivateMember(id: Long)

    @Query("SELECT COUNT(*) FROM members WHERE isActive = 1")
    fun getTotalMembersCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM members WHERE familyId = :familyId AND isActive = 1")
    fun getMemberCountByFamily(familyId: Long): Int

    @Query("SELECT * FROM members ORDER BY name ASC")
    suspend fun getAllMembersList(): List<MemberEntity>
}
