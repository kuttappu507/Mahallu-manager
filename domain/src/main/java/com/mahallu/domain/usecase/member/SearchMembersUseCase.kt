package com.mahallu.domain.usecase.member

import com.mahallu.domain.model.MemberWithFamily
import com.mahallu.domain.repository.MemberRepository
import javax.inject.Inject

class SearchMembersUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    suspend operator fun invoke(query: String): List<MemberWithFamily> {
        return memberRepository.searchMembers(query)
    }
}
