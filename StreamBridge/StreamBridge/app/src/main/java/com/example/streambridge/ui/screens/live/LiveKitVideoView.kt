
package com.example.streambridge.ui.screens.live

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.streambridge.data.realtime.LiveKitManager
import io.livekit.android.renderer.TextureViewRenderer
import io.livekit.android.room.track.VideoTrack
import livekit.org.webrtc.RendererCommon

@Composable
fun LiveKitVideoView(
    videoTrack: VideoTrack?,
    liveKitManager: LiveKitManager,
    modifier: Modifier = Modifier,
    mirror: Boolean = true
) {
    val context = LocalContext.current

    val renderer = remember(context) {
        TextureViewRenderer(context).apply {
            setScalingType(
                RendererCommon.ScalingType.SCALE_ASPECT_FILL
            )
            setEnableHardwareScaler(false)
            setMirror(mirror)
        }
    }

    // Initialize using the same Room that owns the video track.
    DisposableEffect(renderer, liveKitManager) {
        liveKitManager.initializeVideoRenderer(renderer)

        onDispose {
          //  videoTrack?.removeRenderer(renderer)
            renderer.release()
        }
    }

    // Attach and detach the current video track.
    DisposableEffect(renderer, videoTrack) {
        videoTrack?.addRenderer(renderer)

        onDispose {
            videoTrack?.removeRenderer(renderer)
        }
    }

    AndroidView(
        factory = { renderer },
        modifier = modifier
    )
}
