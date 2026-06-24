package com.mahallu.manager.domain.repository

import com.mahallu.manager.domain.model.Member
import kotlinx.coroutines.flow.Flow

interface MemberRepository {
    fun getAllMembers(): Flow<List<Member>>
    fun getMemberById(id: Int): Flow<Member?>
    suspend fun insertMember(member: Member)
    suspend fun updateMember(member: Member)
    suspend fun deleteMember(member: Member)
    fun getMembersByFamilyId(familyId: Int): Flow<List<Member>>
    fun searchMembers(query: String): Flow<List<Member>>
}
