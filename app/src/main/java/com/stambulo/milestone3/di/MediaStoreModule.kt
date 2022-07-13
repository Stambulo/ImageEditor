package com.stambulo.milestone3.di

import android.content.Context
import com.stambulo.milestone3.data.ImageRepositoryImpl
import com.stambulo.milestone3.data.MediaStoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class MediaStoreModule {

    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext application: Context): Context = application

    @Provides
    @Singleton
    fun provideService(application: Context): MediaStoreService = MediaStoreService(application)

    @Provides
    @Singleton
    fun provideRepository(service: MediaStoreService): ImageRepositoryImpl =
        ImageRepositoryImpl(service)
}
