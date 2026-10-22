package com.example.streambridge.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.streambridge.data.remote.RetrofitClient
import com.example.streambridge.data.repository.MediaRepository

class MediaViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(MediaViewModel::class.java)) {
            val repository = MediaRepository(
                RetrofitClient.mediaApi
            )

            return MediaViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}