package com.mahallu.domain.usecase.member

import com.mahallu.domain.model.Member
import com.mahallu.domain.repository.MemberRepository
import javax.inject.Inject

class GetMemberByIdUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(memberId: Int): Member? {
        return memberRepository.getMemberById(memberId)
    }
}
