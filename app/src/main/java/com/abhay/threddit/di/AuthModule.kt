package com.abhay.threddit.di

import com.abhay.threddit.data.firebase_auth.AccountServiceImpl
import com.abhay.threddit.domain.AccountService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAccountService() : AccountService {
        return AccountServiceImpl()
    }

}