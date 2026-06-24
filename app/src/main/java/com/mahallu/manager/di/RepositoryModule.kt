package com.mahallu.manager.di

import com.mahallu.manager.data.repository.*
import com.mahallu.manager.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFamilyRepository(impl: FamilyRepositoryImpl): FamilyRepository

    @Binds
    @Singleton
    abstract fun bindMemberRepository(impl: MemberRepositoryImpl): MemberRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindDonationRepository(impl: DonationRepositoryImpl): DonationRepository

    @Binds
    @Singleton
    abstract fun bindFinanceRepository(impl: FinanceRepositoryImpl): FinanceRepository

    @Binds
    @Singleton
    abstract fun bindMarriageRepository(impl: MarriageRepositoryImpl): MarriageRepository

    @Binds
    @Singleton
    abstract fun bindDeathRepository(impl: DeathRepositoryImpl): DeathRepository

    @Binds
    @Singleton
    abstract fun bindWelfareRepository(impl: WelfareRepositoryImpl): WelfareRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}
