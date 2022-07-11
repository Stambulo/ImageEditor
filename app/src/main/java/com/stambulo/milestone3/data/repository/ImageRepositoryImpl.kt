package com.stambulo.milestone3.data.repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.stambulo.milestone3.data.MediaStoreImage
import com.stambulo.milestone3.data.ViewType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class ImageRepositoryImpl(val application: Application){
    private var prevDate: String = ""
    private var headerId = 1

    suspend fun queryImages(): List<MediaStoreImage> {
        Log.i(">>>", "queryImages")
        val images = mutableListOf<MediaStoreImage>()

        withContext(Dispatchers.IO) {

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
            )
            val selection = "${MediaStore.Images.Media.DATE_ADDED} >= ?"
            val selectionArgs =
                arrayOf(dateToTimestamp(day = 22, month = 10, year = 2008).toString())
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            application.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateModifiedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                Log.i(">>>", "Found ${cursor.count} images")
                while (cursor.moveToNext()) {

                    // Here we'll use the column indexs that we found above.
                    val id = cursor.getLong(idColumn)
                    val dateModified =
                        Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                    val displayName = cursor.getString(displayNameColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    if (prevDate != DateFormat.getDateInstance().format(dateModified)) {
                        prevDate = DateFormat.getDateInstance().format(dateModified)
                        val header =
                            MediaStoreImage(headerId++.toLong(), displayName, dateModified, contentUri, ViewType.HEADER)
                        images += header
                    }

                    val item =
                        MediaStoreImage(id, displayName, dateModified, contentUri)
                    images += item
                    Log.i(">>>", "Added image: $item")
                }
            }
        }

        Log.i(">>>", "Found ${images.size} images")
        return images
    }

    @Suppress("SameParameterValue")
    @SuppressLint("SimpleDateFormat")
    private fun dateToTimestamp(day: Int, month: Int, year: Int): Long =
        SimpleDateFormat("dd.MM.yyyy").let { formatter ->
            TimeUnit.MICROSECONDS.toSeconds(formatter.parse("$day.$month.$year")?.time ?: 0)
        }

    fun ContentResolver.registerObserver(
        uri: Uri,
        observer: (selfChange: Boolean) -> Unit
    ): ContentObserver {
        val contentObserver = object : ContentObserver(Handler()) {
            override fun onChange(selfChange: Boolean) {
                observer(selfChange)
            }
        }
        registerContentObserver(uri, true, contentObserver)
        return contentObserver
    }
}
