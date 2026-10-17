package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.ConnectionStatus
import com.example.ui.theme.NeonLime
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Particle(
    val baseAngle: Float,
    val radialFactor: Float, // 0f (inner) to 1f (outer)
    val size: Float,
    val alpha: Float,
    val speedFactor: Float,
    val waveHarmonic: Int,
    val isLimeAccent: Boolean
)

@Composable
fun ParticleRing(
    status: ConnectionStatus,
    modifier: Modifier = Modifier
) {
    // Generate static particle seeds once to prevent GC overhead
    val particles = remember {
        val list = mutableListOf<Particle>()
        val rng = Random(42)
        val count = 1100

        for (i in 0 until count) {
            val angle = rng.nextFloat() * (2f * PI.toFloat())
            // Gaussian-like concentration in the middle of the band
            val rFactor = (rng.nextFloat() + rng.nextFloat()) / 2f
            val size = (0.75f + rng.nextFloat() * 1.6f)
            val alpha = (0.35f + rng.nextFloat() * 0.65f)
            val speed = 0.8f + rng.nextFloat() * 0.4f
            val harmonic = rng.nextInt(3, 8)
            val isLime = rng.nextFloat() > 0.6f

            list.add(
                Particle(
                    baseAngle = angle,
                    radialFactor = rFactor,
                    size = size,
                    alpha = alpha,
                    speedFactor = speed,
                    waveHarmonic = harmonic,
                    isLimeAccent = isLime
                )
            )
        }
        list
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particle_anim")

    val durationMillis = when (status) {
        ConnectionStatus.NotConnected -> 14000
        ConnectionStatus.Connecting -> 5000
        ConnectionStatus.Connected -> 9000
    }

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (status == ConnectionStatus.Connecting) 900 else 2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val baseRadius = size.width * 0.32f * pulseScale
            val bandThickness = size.width * 0.16f

            // In connected mode, draw soft ambient radial background glow behind particles
            if (status == ConnectionStatus.Connected) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonLime.copy(alpha = 0.16f),
                            NeonLime.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width * 0.48f
                    ),
                    radius = size.width * 0.48f,
                    center = center
                )
            } else if (status == ConnectionStatus.Connecting) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            NeonLime.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width * 0.44f
                    ),
                    radius = size.width * 0.44f,
                    center = center
                )
            }

            val rotRad = Math.toRadians(rotationAngle.toDouble()).toFloat()

            for (p in particles) {
                val currentAngle = p.baseAngle + (rotRad * p.speedFactor)

                // Organic boundary waves with multiple harmonics
                val wave1 = sin(currentAngle * p.waveHarmonic) * 7f
                val wave2 = cos(currentAngle * 4f) * 4f
                val waveOffset = wave1 + wave2

                val r = baseRadius + (p.radialFactor * bandThickness) + waveOffset

                val x = center.x + r * cos(currentAngle)
                val y = center.y + r * sin(currentAngle)

                val particleColor = when (status) {
                    ConnectionStatus.NotConnected -> {
                        // Subtle crystalline silver-white
                        Color.White.copy(alpha = p.alpha * 0.7f)
                    }
                    ConnectionStatus.Connecting -> {
                        if (p.isLimeAccent) {
                            NeonLime.copy(alpha = p.alpha * 0.9f)
                        } else {
                            Color.White.copy(alpha = p.alpha * 0.75f)
                        }
                    }
                    ConnectionStatus.Connected -> {
                        if (p.isLimeAccent) {
                            NeonLime.copy(alpha = p.alpha)
                        } else {
                            Color(0xFFE8FFE0).copy(alpha = p.alpha * 0.9f)
                        }
                    }
                }

                drawCircle(
                    color = particleColor,
                    radius = p.size.dp.toPx() * (if (status == ConnectionStatus.Connected) 1.15f else 1f),
                    center = Offset(x, y)
                )
            }
        }
    }
}
