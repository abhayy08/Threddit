package com.abhay.threddit.di

import com.abhay.threddit.data.firebase_auth.AccountServiceImpl
import com.abhay.threddit.domain.AccountService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

}