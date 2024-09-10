package com.yessorae.yabaltravel.module

import com.yessorae.yabaltravel.data.repository.RecommendationRepository
import com.yessorae.yabaltravel.data.repository.RecommendationRepositoryImpl
import com.yessorae.yabaltravel.data.repository.RegionRepository
import com.yessorae.yabaltravel.data.repository.RegionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindRecommendationRepository(recommendationRepository: RecommendationRepositoryImpl): RecommendationRepository

    @Binds
    abstract fun bindRegionRepository(regionRepository: RegionRepositoryImpl): RegionRepository
}