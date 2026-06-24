package com.mahallu.manager.domain.usecase.member

import com.mahallu.manager.domain.model.Member
import com.mahallu.manager.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMembersUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(): Flow<List<Member>> {
        return memberRepository.getAllMembers()
    }
}

class GetMemberByIdUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(id: Int): Member? {
        return memberRepository.getMemberById(id)
    }
}

class SaveMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(member: Member) {
        if (member.id == 0) {
            memberRepository.insertMember(member)
        } else {
            memberRepository.updateMember(member)
        }
    }
}

class DeleteMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(member: Member) {
        memberRepository.deleteMember(member)
    }
}

class SearchMembersUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(query: String): Flow<List<Member>> {
        return memberRepository.searchMembers(query)
    }
}

class GetMembersByFamilyIdUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(familyId: Int): Flow<List<Member>> {
        return memberRepository.getMembersByFamilyId(familyId)
    }
}
