package com.mahallu.domain.usecase.member

import com.mahallu.domain.model.Member
import com.mahallu.domain.repository.MemberRepository
import javax.inject.Inject

class AddMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(member: Member) {
        memberRepository.insertMember(member)
    }
}
