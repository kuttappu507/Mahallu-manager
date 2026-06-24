package com.mahallu.core.database.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.mahallu.core.database.entity.Gender
import com.mahallu.core.database.entity.Member
import com.mahallu.core.database.entity.MemberStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members WHERE status = :status ORDER BY name")
    fun getAllMembers(status: MemberStatus = MemberStatus.ACTIVE): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE familyId = :familyId ORDER BY name")
    fun getMembersByFamily(familyId: Long): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Long): Member?

    @Query("SELECT * FROM members WHERE name LIKE :query OR mobile LIKE :query OR email LIKE :query")
    fun searchMembers(query: String): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE gender = :gender AND status = :status")
    fun getMembersByGender(gender: Gender, status: MemberStatus = MemberStatus.ACTIVE): Flow<List<Member>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)

    @Query("UPDATE members SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateMemberStatus(id: Long, status: MemberStatus, updatedAt: java.util.Date)

    @Query("SELECT COUNT(*) FROM members WHERE status = :status")
    suspend fun countMembersByStatus(status: MemberStatus = MemberStatus.ACTIVE): Int

    @Query("SELECT COUNT(*) FROM members WHERE familyId = :familyId")
    suspend fun countMembersByFamily(familyId: Long): Int

    @Query("SELECT * FROM members ORDER BY createdAt DESC LIMIT 10")
    suspend fun getRecentMembers(): List<Member>
}
