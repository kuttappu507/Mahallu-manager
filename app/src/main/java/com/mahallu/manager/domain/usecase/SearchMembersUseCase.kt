package com.mahallu.manager.domain.usecase

import com.mahallu.manager.domain.model.Member
import com.mahallu.manager.domain.repository.MemberRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMembersUseCase @Inject constructor(
    private val memberRepository: MemberRepository
) {
    operator fun invoke(query: String): Flow<List<Member>> {
        return memberRepository.searchMembers(query)
    }
}
