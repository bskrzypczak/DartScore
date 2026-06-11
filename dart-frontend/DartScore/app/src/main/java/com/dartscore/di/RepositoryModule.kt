package com.dartscore.di

import com.dartscore.feature.auth.data.AuthRepository
import com.dartscore.feature.auth.data.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Wiązania repozytoriów. To JEDYNE miejsce, które wie, której implementacji używamy.
// Zmiana atrapy -> Firebase to dosłownie zamiana typu w tej jednej linii.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository
}