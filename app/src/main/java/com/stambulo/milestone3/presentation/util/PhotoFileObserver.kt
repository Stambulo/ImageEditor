package com.stambulo.milestone3.presentation.util

import android.R.attr
import android.os.FileObserver
import android.util.Log
import com.stambulo.milestone3.presentation.fragments.GalleryFragment

class PhotoFileObserver(path: String, val galleryFragment: GalleryFragment): FileObserver(path) {

    override fun onEvent(event: Int, path: String?) {
        if (path != null) {
            when (event) {
                CREATE -> Log.i(">>>", "FileObserver CREATE:" + attr.path)
                DELETE -> Log.i(">>>", "FileObserver DELETE:" + attr.path)
                DELETE_SELF -> Log.i(">>>", "FileObserver DELETE_SELF:" + attr.path)
                MODIFY -> Log.i(">>>", "FileObserver MODIFY:" + attr.path)
                MOVED_FROM -> Log.i(">>>", "FileObserver MOVED_FROM:" + attr.path)
                MOVED_TO -> Log.i(">>>", "FileObserver MOVED_TO:" + attr.path)
                MOVE_SELF -> Log.i(">>>", "FileObserver MOVE_SELF:" + attr.path)
                else -> {}
            }
            galleryFragment.refreshGallery()
        }
    }

    fun close() {
        super.finalize()
    }
}
