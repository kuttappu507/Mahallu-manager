package com.mahallu.core.database.dao

import androidx.room.*
import com.mahallu.core.database.entity.Gender
import com.mahallu.core.database.entity.Member
import com.mahallu.core.database.entity.MemberStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members ORDER BY name ASC")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE familyId = :familyId ORDER BY name ASC")
    fun getMembersByFamily(familyId: Long): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): Member?

    @Query("SELECT * FROM members WHERE name LIKE :query OR mobile LIKE :query OR email LIKE :query")
    fun searchMembers(query: String): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE gender = :gender ORDER BY name ASC")
    fun getMembersByGender(gender: Gender): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE status = :status ORDER BY name ASC")
    fun getMembersByStatus(status: MemberStatus): Flow<List<Member>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("SELECT COUNT(*) FROM members")
    suspend fun getTotalMembersCount(): Int

    @Query("SELECT COUNT(*) FROM members WHERE familyId = :familyId")
    suspend fun getMembersCountByFamily(familyId: Long): Int

    @Query("SELECT COUNT(*) FROM members WHERE status = :status")
    suspend fun getMembersCountByStatus(status: MemberStatus): Int

    @Query("SELECT * FROM members LIMIT :limit OFFSET :offset")
    suspend fun getMembersPaged(limit: Int, offset: Int): List<Member>
}
