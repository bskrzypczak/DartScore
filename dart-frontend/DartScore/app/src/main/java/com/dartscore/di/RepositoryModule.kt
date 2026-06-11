package com.dartscore.di

import com.dartscore.feature.auth.data.AuthRepository
import com.dartscore.feature.auth.data.FakeAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Wiązania repozytoriów. Zmiana atrapy na Firebase = podmiana jednej linii tutaj.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FakeAuthRepository): AuthRepository
}
