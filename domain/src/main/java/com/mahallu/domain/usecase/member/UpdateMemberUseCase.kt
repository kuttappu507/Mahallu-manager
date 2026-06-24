package com.mahallu.domain.usecase.member

import com.mahallu.domain.model.Member
import com.mahallu.domain.repository.MemberRepository
import javax.inject.Inject

class UpdateMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(member: Member) {
        memberRepository.updateMember(member)
    }
}
