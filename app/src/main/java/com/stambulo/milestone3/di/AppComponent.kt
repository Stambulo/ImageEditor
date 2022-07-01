package com.stambulo.milestone3.di

import com.stambulo.milestone3.view.viewmodels.GalleryViewModel
import dagger.Component

@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(galleryViewModel: GalleryViewModel)
}
