package com.mahallu.domain.usecase.member

import com.mahallu.domain.repository.MemberRepository
import javax.inject.Inject

class DeleteMemberUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(memberId: Int) {
        memberRepository.deleteMember(memberId)
    }
}
