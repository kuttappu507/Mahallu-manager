package com.mahallu.domain.repository

import com.mahallu.domain.model.Member
import com.mahallu.domain.model.MemberWithFamily

interface MemberRepository {
    suspend fun getAllMembersWithFamily(): List<MemberWithFamily>
    suspend fun getMemberById(memberId: Int): Member?
    suspend fun searchMembers(query: String): List<MemberWithFamily>
    suspend fun insertMember(member: Member)
    suspend fun updateMember(member: Member)
    suspend fun deleteMember(memberId: Int)
    suspend fun getMembersByFamilyId(familyId: Int): List<Member>
}
