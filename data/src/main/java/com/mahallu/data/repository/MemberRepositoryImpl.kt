package com.mahallu.data.repository

import com.mahallu.domain.model.Member
import com.mahallu.domain.model.MemberWithFamily
import com.mahallu.domain.repository.MemberRepository
import com.mahallu.data.local.dao.MemberDao
import com.mahallu.data.local.dao.FamilyDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepositoryImpl @Inject constructor(
    private val memberDao: MemberDao,
    private val familyDao: FamilyDao
) : MemberRepository {

    override suspend fun getAllMembersWithFamily(): List<MemberWithFamily> = withContext(Dispatchers.IO) {
        memberDao.getAllMembersWithFamily()
    }

    override suspend fun getMemberById(memberId: Int): Member? = withContext(Dispatchers.IO) {
        memberDao.getMemberById(memberId)
    }

    override suspend fun searchMembers(query: String): List<MemberWithFamily> = withContext(Dispatchers.IO) {
        memberDao.searchMembers("%$query%")
    }

    override suspend fun insertMember(member: Member) = withContext(Dispatchers.IO) {
        memberDao.insertMember(member)
    }

    override suspend fun updateMember(member: Member) = withContext(Dispatchers.IO) {
        memberDao.updateMember(member)
    }

    override suspend fun deleteMember(memberId: Int) = withContext(Dispatchers.IO) {
        memberDao.deleteMember(memberId)
    }

    override suspend fun getMembersByFamilyId(familyId: Int): List<Member> = withContext(Dispatchers.IO) {
        memberDao.getMembersByFamilyId(familyId)
    }
}
