package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CountryFlag(
    countryCode: String,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .border(1.dp, Color(0x33FFFFFF), CircleShape)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = this.size.width
            val height = this.size.height

            when (countryCode.uppercase()) {
                "US" -> {
                    // 13 stripes (red & white)
                    val stripeHeight = height / 13f
                    val redColor = Color(0xFFB22234)
                    val whiteColor = Color.White
                    val blueCanton = Color(0xFF3C3B6E)

                    for (i in 0 until 13) {
                        drawRect(
                            color = if (i % 2 == 0) redColor else whiteColor,
                            topLeft = Offset(0f, i * stripeHeight),
                            size = Size(width, stripeHeight + 0.5f)
                        )
                    }

                    // Canton (top-left rectangle)
                    val cantonWidth = width * 0.48f
                    val cantonHeight = stripeHeight * 7f
                    drawRect(
                        color = blueCanton,
                        topLeft = Offset.Zero,
                        size = Size(cantonWidth, cantonHeight)
                    )

                    // Draw star representation dots
                    val cols = 4
                    val rows = 3
                    val stepX = cantonWidth / (cols + 1)
                    val stepY = cantonHeight / (rows + 1)
                    for (r in 1..rows) {
                        for (c in 1..cols) {
                            drawCircle(
                                color = Color.White,
                                radius = 1.2.dp.toPx(),
                                center = Offset(c * stepX, r * stepY)
                            )
                        }
                    }
                }
                "DE" -> {
                    // Germany: Black, Red, Gold horizontal stripes
                    val stripeH = height / 3f
                    drawRect(Color(0xFF000000), Offset(0f, 0f), Size(width, stripeH))
                    drawRect(Color(0xFFDD0000), Offset(0f, stripeH), Size(width, stripeH))
                    drawRect(Color(0xFFFFCE00), Offset(0f, stripeH * 2), Size(width, stripeH))
                }
                "GB" -> {
                    // UK: Blue field with Union Jack style crosses
                    drawRect(Color(0xFF012169), Offset.Zero, Size(width, height))
                    // White diagonals
                    drawLine(Color.White, Offset.Zero, Offset(width, height), strokeWidth = width * 0.16f)
                    drawLine(Color.White, Offset(width, 0f), Offset(0f, height), strokeWidth = width * 0.16f)
                    // Red diagonals
                    drawLine(Color(0xFFC8102E), Offset.Zero, Offset(width, height), strokeWidth = width * 0.08f)
                    drawLine(Color(0xFFC8102E), Offset(width, 0f), Offset(0f, height), strokeWidth = width * 0.08f)
                    // White cross
                    drawRect(Color.White, Offset(width * 0.4f, 0f), Size(width * 0.2f, height))
                    drawRect(Color.White, Offset(0f, height * 0.4f), Size(width, height * 0.2f))
                    // Red cross
                    drawRect(Color(0xFFC8102E), Offset(width * 0.44f, 0f), Size(width * 0.12f, height))
                    drawRect(Color(0xFFC8102E), Offset(0f, height * 0.44f), Size(width, height * 0.12f))
                }
                "CA" -> {
                    // Canada: Red, White, Red with maple leaf dot
                    drawRect(Color(0xFFD80027), Offset(0f, 0f), Size(width * 0.28f, height))
                    drawRect(Color.White, Offset(width * 0.28f, 0f), Size(width * 0.44f, height))
                    drawRect(Color(0xFFD80027), Offset(width * 0.72f, 0f), Size(width * 0.28f, height))
                    drawCircle(Color(0xFFD80027), radius = width * 0.12f, center = Offset(width * 0.5f, height * 0.5f))
                }
                "NL" -> {
                    // Netherlands: Red, White, Blue
                    val stripeH = height / 3f
                    drawRect(Color(0xFFAE1C28), Offset(0f, 0f), Size(width, stripeH))
                    drawRect(Color.White, Offset(0f, stripeH), Size(width, stripeH))
                    drawRect(Color(0xFF21468B), Offset(0f, stripeH * 2), Size(width, stripeH))
                }
                "JP" -> {
                    // Japan: White background, Red circle
                    drawRect(Color.White, Offset.Zero, Size(width, height))
                    drawCircle(Color(0xFFBC002D), radius = width * 0.28f, center = Offset(width * 0.5f, height * 0.5f))
                }
                "FR" -> {
                    // France: Blue, White, Red vertical stripes
                    val stripeW = width / 3f
                    drawRect(Color(0xFF002395), Offset(0f, 0f), Size(stripeW, height))
                    drawRect(Color.White, Offset(stripeW, 0f), Size(stripeW, height))
                    drawRect(Color(0xFFED2939), Offset(stripeW * 2, 0f), Size(stripeW, height))
                }
                "SG" -> {
                    // Singapore: Red top with white crescent/stars, White bottom
                    drawRect(Color(0xFFED2939), Offset(0f, 0f), Size(width, height * 0.5f))
                    drawRect(Color.White, Offset(0f, height * 0.5f), Size(width, height * 0.5f))
                    drawCircle(Color.White, radius = width * 0.12f, center = Offset(width * 0.3f, height * 0.26f))
                }
                else -> {
                    drawRect(Color(0xFF334155), Offset.Zero, Size(width, height))
                }
            }
        }
    }
}
