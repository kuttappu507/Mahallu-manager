package com.mahallu.manager.domain.usecase

import com.mahallu.manager.domain.model.Member
import com.mahallu.manager.domain.repository.MemberRepository
import javax.inject.Inject

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
