package com.abhay.threddit.di

import com.abhay.threddit.data.firebase.auth.AccountServiceImpl
import com.abhay.threddit.data.firebase.firestore.FirestoreServiceImpl
import com.abhay.threddit.domain.AccountService
import com.abhay.threddit.domain.FirestoreService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
    @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

    @Binds abstract fun provideFirestoreService(impl: FirestoreServiceImpl): FirestoreService

}