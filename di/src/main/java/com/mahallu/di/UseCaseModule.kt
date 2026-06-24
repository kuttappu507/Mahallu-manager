package com.mahallu.di

import com.mahallu.domain.repository.FamilyRepository
import com.mahallu.domain.repository.MemberRepository
import com.mahallu.domain.usecase.family.*
import com.mahallu.domain.usecase.member.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetAllFamiliesUseCase(repository: FamilyRepository): GetAllFamiliesUseCase {
        return GetAllFamiliesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetFamilyByIdUseCase(repository: FamilyRepository): GetFamilyByIdUseCase {
        return GetFamilyByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddFamilyUseCase(repository: FamilyRepository): AddFamilyUseCase {
        return AddFamilyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateFamilyUseCase(repository: FamilyRepository): UpdateFamilyUseCase {
        return UpdateFamilyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteFamilyUseCase(repository: FamilyRepository): DeleteFamilyUseCase {
        return DeleteFamilyUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchFamiliesUseCase(repository: FamilyRepository): SearchFamiliesUseCase {
        return SearchFamiliesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllMembersUseCase(repository: MemberRepository): GetAllMembersUseCase {
        return GetAllMembersUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMemberByIdUseCase(repository: MemberRepository): GetMemberByIdUseCase {
        return GetMemberByIdUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideAddMemberUseCase(repository: MemberRepository): AddMemberUseCase {
        return AddMemberUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideUpdateMemberUseCase(repository: MemberRepository): UpdateMemberUseCase {
        return UpdateMemberUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideDeleteMemberUseCase(repository: MemberRepository): DeleteMemberUseCase {
        return DeleteMemberUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSearchMembersUseCase(repository: MemberRepository): SearchMembersUseCase {
        return SearchMembersUseCase(repository)
    }
}
