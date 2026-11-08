package com.example.streambridge.data.realtime

import android.content.Context
import io.livekit.android.LiveKit
import io.livekit.android.renderer.TextureViewRenderer
import io.livekit.android.room.Room
import io.livekit.android.room.participant.LocalParticipant
import io.livekit.android.room.track.Track
import io.livekit.android.room.track.VideoTrack
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import io.livekit.android.room.track.LocalVideoTrack

class LiveKitManager(context: Context) {

    private val appContext = context.applicationContext

    // Hamesha ek Room instance ready rakhenge (connected ho ya offline/preview mode me ho)
    private var room: Room = LiveKit.create(appContext)

    private val _roomState = MutableStateFlow<Room?>(room)
    val roomState: StateFlow<Room?> = _roomState.asStateFlow()

    val currentRoom: Room?
        get() = room

    val localParticipant: LocalParticipant
        get() = room.localParticipant

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

            // Existing room object ko hi connect karenge (taaki agar camera on tha, toh wo continue rahe)
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
        // Ye bina Room connect kiye bhi successfully device camera start kar dega
        room.localParticipant.setCameraEnabled(enabled)
    }

    fun disconnect() {
        room.disconnect()

        // Disconnect hone ke baad, agle session/preview ke liye fresh Room instance bana kar rakh lo
        room = LiveKit.create(appContext)
        _roomState.value = room
    }

    fun getLocalCameraTrack(): VideoTrack? {
        val participant = room.localParticipant

        // 1. Pehle standard tarike se try karo (jab source CAMERA mark ho)
        val standardTrackPub = participant.getTrackPublication(Track.Source.CAMERA)
        val standardTrack = standardTrackPub?.track as? VideoTrack
        if (standardTrack != null) return standardTrack

        // 2. Offline Mode Fallback: Bina .second use kiye seedha values me dhoondho
        for (publication in participant.trackPublications.values) {
            val track = publication.track
            if (track is VideoTrack) {
                return track
            }
        }

        return null
    }


    // Class ke top variables me ek offline track ka reference banao
    private var offlineVideoTrack: LocalVideoTrack? = null

// --- Ye 3 naye functions class me daal do ---

// Ensure ye import added ho


    fun createOfflineCameraTrack(): VideoTrack {
        // 1. Participant ke public function se track banaya (koi internal error nahi aayega)
        val track = room.localParticipant.createVideoTrack(
            name = "offline_preview"
        )

        // 2. Hardware lens ko explicitly frame capture karne ka command diya (Black screen fixed)
        track.startCapture()

        offlineVideoTrack = track
        return track
    }

    fun releaseOfflineTrack() {
        offlineVideoTrack?.stop()
        offlineVideoTrack?.dispose() // <-- 'release()' ki jagah 'dispose()' aayega
        offlineVideoTrack = null
    }

    suspend fun publishOfflineTrack() {
        // Jab user "Go Live" click karega aur room connect ho jayega,
        // tab is local track ko network pe publish karenge
        offlineVideoTrack?.let { track ->
            room.localParticipant.publishVideoTrack(track)
        }
    }



    fun initializeVideoRenderer(renderer: TextureViewRenderer) {
        room.initVideoRenderer(renderer)
    }


}