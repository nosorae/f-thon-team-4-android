package com.yessorae.yabaltravel.module

import android.content.Context
import com.yessorae.yabaltravel.data.repository.SetUpRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SetUpRepoModule {

    @Provides
    fun provideSetUpRepo(@ApplicationContext context: Context): SetUpRepository {
        return SetUpRepository(context)
    }
}