package com.example.streambridge.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.streambridge.data.remote.RetrofitClient
import com.example.streambridge.data.repository.MediaRepository
import com.example.streambridge.data.realtime.LiveKitManager
import com.example.streambridge.data.realtime.LiveKitManagerProvider

class MediaViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {

            val repository = MediaRepository(
                RetrofitClient.mediaApi
            )

            val liveKitManager = LiveKitManagerProvider.get(appContext)

            return MediaViewModel(
                repository,
                liveKitManager
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}