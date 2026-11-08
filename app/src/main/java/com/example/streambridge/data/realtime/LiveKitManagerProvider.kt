package com.example.streambridge.data.realtime


import android.content.Context

object LiveKitManagerProvider {

    @Volatile
    private var instance: LiveKitManager? = null

    fun get(context: Context): LiveKitManager {
        return instance ?: synchronized(this) {
            instance ?: LiveKitManager(context.applicationContext).also {
                instance = it
            }
        }
    }
}