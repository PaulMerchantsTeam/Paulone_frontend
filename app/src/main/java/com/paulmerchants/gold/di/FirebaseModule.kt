package com.paulmerchants.gold.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {

    @Provides
    fun provideAnalytics(): FirebaseAnalytics {
        return Firebase.analytics
    }

    @Provides
    fun provideAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    fun provideRemoteConfig():FirebaseRemoteConfig{
        return Firebase.remoteConfig
    }


}