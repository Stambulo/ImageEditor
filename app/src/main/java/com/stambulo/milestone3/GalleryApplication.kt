package com.stambulo.milestone3

import android.app.Application
import com.stambulo.milestone3.di.AppComponent
import com.stambulo.milestone3.di.AppModule
import com.stambulo.milestone3.di.DaggerAppComponent

class GalleryApplication: Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent
            .builder()
            .appModule(AppModule(context = this))
            .build()
    }
}
