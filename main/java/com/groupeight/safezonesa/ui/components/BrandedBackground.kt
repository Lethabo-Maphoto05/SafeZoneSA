package com.groupeight.safezonesa.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.groupeight.safezonesa.ui.theme.BrightBlue
import com.groupeight.safezonesa.ui.theme.Navy
import kotlin.random.Random

/**
 * A drawn (not photographic) background used behind Splash, Onboarding, Login and Register —
 * a deep-navy gradient with a faint city skyline silhouette and circuit-style accent lines,
 * echoing the shield logo's own artwork. Drawn with Canvas rather than a bundled stock photo
 * so there's no image-licensing question and it scales losslessly to any screen size.
 */
@Composable
fun BrandedBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Navy, Color(0xFF0D2A54), Navy),
                    startY = 0f,
                    endY = h
                )
            )

            // Faint circuit-style accent lines, upper portion of the screen.
            val rng = Random(42) // fixed seed: identical, calm pattern every recomposition
            repeat(10) {
                val startX = rng.nextFloat() * w
                val startY = rng.nextFloat() * h * 0.55f
                val length = w * (0.1f + rng.nextFloat() * 0.2f)
                val horizontal = rng.nextBoolean()
                val end = if (horizontal) Offset(startX + length, startY) else Offset(startX, startY + length)
                drawLine(
                    color = BrightBlue.copy(alpha = 0.10f),
                    start = Offset(startX, startY),
                    end = end,
                    strokeWidth = 1.5f
                )
            }

            // Simple skyline silhouette anchored to the bottom edge, echoing the logo's shield.
            val buildingCount = 7
            val baseY = h * 0.92f
            val slotWidth = w / buildingCount
            for (i in 0 until buildingCount) {
                val bw = slotWidth * (0.55f + rng.nextFloat() * 0.25f)
                val bh = h * (0.06f + rng.nextFloat() * 0.14f)
                val bx = slotWidth * i + (slotWidth - bw) / 2f
                drawRect(
                    color = Color.Black.copy(alpha = 0.18f),
                    topLeft = Offset(bx, baseY - bh),
                    size = androidx.compose.ui.geometry.Size(bw, bh)
                )
            }
            drawLine(
                color = Color.Black.copy(alpha = 0.18f),
                start = Offset(0f, baseY),
                end = Offset(w, baseY),
                strokeWidth = 2f
            )

            // Soft radial glow behind where the logo/title typically sits, for visual depth.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(BrightBlue.copy(alpha = 0.18f), Color.Transparent),
                    center = Offset(w / 2f, h * 0.32f),
                    radius = w * 0.7f
                ),
                radius = w * 0.7f,
                center = Offset(w / 2f, h * 0.32f)
            )
        }
        content()
    }
}
