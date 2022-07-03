package com.stambulo.milestone3.view.viewmodels

import android.app.Application
import android.app.RecoverableSecurityException
import android.content.IntentSender
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.stambulo.milestone3.data.MediaStoreImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    private var pendingDeleteImage: MediaStoreImage? = null
    private val _permissionNeededForDelete = MutableLiveData<IntentSender?>()

    suspend fun performDeleteImage(image: MediaStoreImage) {
        withContext(Dispatchers.IO) {
            try {
                getApplication<Application>().contentResolver.delete(
                    image.contentUri,
                    "${MediaStore.Images.Media._ID} = ?",
                    arrayOf(image.id.toString())
                )
            } catch (securityException: SecurityException) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val recoverableSecurityException =
                        securityException as? RecoverableSecurityException
                            ?: throw securityException

                    // Signal to the Activity that it needs to request permission and
                    // try the delete again if it succeeds.
                    pendingDeleteImage = image
                    _permissionNeededForDelete.postValue(
                        recoverableSecurityException.userAction.actionIntent.intentSender
                    )
                } else {
                    throw securityException
                }
            }
        }
    }
}
