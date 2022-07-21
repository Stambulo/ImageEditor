package com.stambulo.milestone3.di

import android.widget.ImageView
import com.stambulo.milestone3.presentation.image.IImageLoader
import com.stambulo.milestone3.presentation.image.ImageLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GlideModule {

    @Singleton
    @Provides
    fun provideImageLoader(): IImageLoader<ImageView> = ImageLoader()
}
