package com.dartscore.di

import com.dartscore.feature.auth.data.AuthRepository
import com.dartscore.feature.auth.data.FirebaseAuthRepository
import com.dartscore.feature.friends.data.FirestoreFriendsRepository
import com.dartscore.feature.friends.data.FriendsRepository
import com.dartscore.feature.play.data.FirestoreMatchRepository
import com.dartscore.feature.play.data.MatchRepository
import com.dartscore.feature.profile.data.FirestoreProfileRepository
import com.dartscore.feature.profile.data.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Wiązania repozytoriów – jedyne miejsce, które wie, których implementacji używamy.
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: FirestoreProfileRepository): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(impl: FirestoreMatchRepository): MatchRepository

    @Binds
    @Singleton
    abstract fun bindFriendsRepository(impl: FirestoreFriendsRepository): FriendsRepository
}