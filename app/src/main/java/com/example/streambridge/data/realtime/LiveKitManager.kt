
package com.example.streambridge.data.realtime

import android.content.Context
import io.livekit.android.LiveKit
import io.livekit.android.renderer.TextureViewRenderer
import io.livekit.android.room.Room
import io.livekit.android.room.participant.LocalParticipant
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import io.livekit.android.room.track.LocalVideoTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LiveKitManager(context: Context) {

    private val appContext = context.applicationContext

    // Single Room instance for preview and live session
    private var room: Room = LiveKit.create(appContext)

    private val _roomState = MutableStateFlow<Room?>(room)
    val roomState: StateFlow<Room?> = _roomState.asStateFlow()

    val currentRoom: Room?
        get() = room

    val localParticipant: LocalParticipant
        get() = room.localParticipant

    private var offlineVideoTrack: LocalVideoTrack? = null

    suspend fun connect(
        serverUrl: String,
        token: String
    ): Result<Unit> {
        return try {
            if (room.state == Room.State.CONNECTED) {
                return Result.failure(
                    IllegalStateException("Already connected to a LiveKit room")
                )
            }

            room.connect(
                url = serverUrl,
                token = token
            )

            _roomState.value = room
            Result.success(Unit)

        } catch (e: Exception) {
            room.disconnect()
            Result.failure(e)
        }
    }

    suspend fun setMicrophoneEnabled(enabled: Boolean) {
        room.localParticipant.setMicrophoneEnabled(enabled)
    }

    suspend fun setCameraEnabled(enabled: Boolean) {
        room.localParticipant.setCameraEnabled(enabled)
    }

    fun disconnect() {
        room.disconnect()

        // Prepare a fresh Room for the next session
        room = LiveKit.create(appContext)
        _roomState.value = room
    }

    fun getLocalCameraTrack(): VideoTrack? {
        val participant = room.localParticipant

        // Standard camera publication
        val standardTrackPub =
            participant.getTrackPublication(Track.Source.CAMERA)

        val standardTrack = standardTrackPub?.track as? VideoTrack

        if (standardTrack != null) return standardTrack

        // Offline preview fallback
        for (publication in participant.trackPublications.values) {
            val track = publication.track
            if (track is VideoTrack) {
                return track
            }
        }

        return null
    }

    fun createOfflineCameraTrack(): VideoTrack {
        // Reuse existing preview track
        offlineVideoTrack?.let {
            return it
        }

        val track = room.localParticipant.createVideoTrack(
            name = "offline_preview"
        )

        track.startCapture()

        offlineVideoTrack = track
        return track
    }

    fun releaseOfflineTrack() {
        offlineVideoTrack?.stop()
        offlineVideoTrack?.dispose()
        offlineVideoTrack = null
    }

    suspend fun publishOfflineTrack() {
        offlineVideoTrack?.let { track ->
            room.localParticipant.publishVideoTrack(track)
        }
    }

    fun initializeVideoRenderer(renderer: TextureViewRenderer) {
        room.initVideoRenderer(renderer)
    }
}
