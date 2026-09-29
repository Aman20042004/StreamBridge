package com.example.streambridge.ui.screens.splash


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SplashScreen(
    onGetStarted: () -> Unit = {},
    onLogin: () -> Unit = {}
) {

    val backgroundColor = Color(0xFF020817)
    val blue = Color(0xFF2563FF)
    val purple = Color(0xFF6D28D9)
    val pink = Color(0xFFD946EF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {

        // Background neon artwork
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawNeonBackground(
                blue = blue,
                purple = purple,
                pink = pink
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(1f))

            // Logo
            StreamBridgeLogo()

            Spacer(modifier = Modifier.height(22.dp))

            // App Name
            Text(
                text = "Stream Bridge",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Connect. Stream. Learn Together.",
                color = Color(0xFFB8C2D6),
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(1f))

            // Get Started button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = Color(0xFF7C3AED),
                        spotColor = Color(0xFF2563EB)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF315CFF),
                                Color(0xFF8B2CF5)
                            )
                        )
                    )
                    .clickable {
                        onGetStarted()
                    },
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Get Started",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Login text
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Already have an account?",
                    color = Color(0xFF8E9AB0),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "Login",
                    color = Color(0xFF8B5CF6),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        onLogin()
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}


/* ---------------------------------------------------------
   STREAM BRIDGE LOGO
   --------------------------------------------------------- */

@Composable
private fun StreamBridgeLogo() {

    Box(
        modifier = Modifier.size(86.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.size(86.dp)
        ) {

            // Outer glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x553B82F6),
                        Color(0x221D4ED8),
                        Color.Transparent
                    )
                ),
                radius = size.minDimension / 2
            )

            // Purple-blue bridge shape
            val path = androidx.compose.ui.graphics.Path().apply {

                moveTo(
                    size.width * 0.20f,
                    size.height * 0.35f
                )

                cubicTo(
                    size.width * 0.35f,
                    size.height * 0.10f,
                    size.width * 0.65f,
                    size.height * 0.10f,
                    size.width * 0.80f,
                    size.height * 0.30f
                )

                lineTo(
                    size.width * 0.80f,
                    size.height * 0.55f
                )

                cubicTo(
                    size.width * 0.65f,
                    size.height * 0.35f,
                    size.width * 0.35f,
                    size.height * 0.35f,
                    size.width * 0.20f,
                    size.height * 0.60f
                )

                close()
            }

            drawPath(
                path = path,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8B5CF6),
                        Color(0xFF2563EB),
                        Color(0xFFEC4899)
                    )
                )
            )

            // Inner blue section
            drawCircle(
                color = Color(0xFF0F172A),
                radius = size.width * 0.18f,
                center = Offset(
                    size.width * 0.50f,
                    size.height * 0.48f
                )
            )
        }
    }
}


/* ---------------------------------------------------------
   BACKGROUND
   --------------------------------------------------------- */

private fun DrawScope.drawNeonBackground(
    blue: Color,
    purple: Color,
    pink: Color
) {

    // Top-right blue glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                blue.copy(alpha = 0.30f),
                blue.copy(alpha = 0.08f),
                Color.Transparent
            )
        ),
        radius = 260f,
        center = Offset(
            size.width * 0.90f,
            size.height * 0.30f
        )
    )

    // Bottom-left purple glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                purple.copy(alpha = 0.38f),
                purple.copy(alpha = 0.12f),
                Color.Transparent
            )
        ),
        radius = 280f,
        center = Offset(
            size.width * 0.10f,
            size.height * 0.75f
        )
    )

    // Bottom pink glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                pink.copy(alpha = 0.18f),
                Color.Transparent
            )
        ),
        radius = 220f,
        center = Offset(
            size.width * 0.75f,
            size.height * 0.90f
        )
    )

    // Decorative neon lines
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                purple.copy(alpha = 0.5f),
                blue.copy(alpha = 0.5f)
            )
        ),
        start = Offset(
            size.width * 0.0f,
            size.height * 0.82f
        ),
        end = Offset(
            size.width * 0.95f,
            size.height * 0.55f
        ),
        strokeWidth = 2f
    )

    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                blue.copy(alpha = 0.35f),
                Color.Transparent
            )
        ),
        start = Offset(
            size.width * 0.05f,
            size.height * 0.90f
        ),
        end = Offset(
            size.width * 0.85f,
            size.height * 0.68f
        ),
        strokeWidth = 1.5f
    )
}


@Preview
@Composable
fun SplashPreview(){
    SplashScreen()
}
