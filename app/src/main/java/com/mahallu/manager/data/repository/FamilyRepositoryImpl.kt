package com.mahallu.manager.data.repository

import com.mahallu.manager.data.local.dao.FamilyDao
import com.mahallu.manager.data.local.dao.MemberDao
import com.mahallu.manager.data.local.entity.FamilyEntity
import com.mahallu.manager.data.local.entity.MemberEntity
import com.mahallu.manager.domain.model.Family
import com.mahallu.manager.domain.model.Member
import com.mahallu.manager.domain.repository.FamilyRepository
import com.mahallu.manager.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FamilyRepositoryImpl @Inject constructor(
    private val familyDao: FamilyDao
) : FamilyRepository {

    override fun getAllFamilies(): Flow<List<Family>> {
        return familyDao.getAllFamilies().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getFamilyById(id: Int): Flow<Family?> {
        return familyDao.getFamilyById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertFamily(family: Family) {
        familyDao.insertFamily(family.toEntity())
    }

    override suspend fun updateFamily(family: Family) {
        familyDao.updateFamily(family.toEntity())
    }

    override suspend fun deleteFamily(family: Family) {
        familyDao.deleteFamily(family.toEntity())
    }

    override suspend fun getFamilyByNumber(number: String): Family? {
        return familyDao.getFamilyByNumber(number)?.toDomainModel()
    }

    override fun searchFamilies(query: String): Flow<List<Family>> {
        return familyDao.searchFamilies("%$query%").map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}

@Singleton
class MemberRepositoryImpl @Inject constructor(
    private val memberDao: MemberDao
) : MemberRepository {

    override fun getAllMembers(): Flow<List<Member>> {
        return memberDao.getAllMembers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun getMemberById(id: Int): Flow<Member?> {
        return memberDao.getMemberById(id).map { it?.toDomainModel() }
    }

    override suspend fun insertMember(member: Member) {
        memberDao.insertMember(member.toEntity())
    }

    override suspend fun updateMember(member: Member) {
        memberDao.updateMember(member.toEntity())
    }

    override suspend fun deleteMember(member: Member) {
        memberDao.deleteMember(member.toEntity())
    }

    override fun getMembersByFamilyId(familyId: Int): Flow<List<Member>> {
        return memberDao.getMembersByFamilyId(familyId).map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override fun searchMembers(query: String): Flow<List<Member>> {
        return memberDao.searchMembers("%$query%").map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}
