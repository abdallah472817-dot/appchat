package com.example.chatapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.chatapp.ui.theme.NeoBrutalistBorderWidth
import com.example.chatapp.ui.theme.NeoBrutalistCornerShape
import com.example.chatapp.ui.theme.NeoBrutalistShadowOffset

enum class NeoBrutalistCardStyle { Paper, Rose }

/**
 * Card with a solid 4 dp offset shadow (no blur), a hard 2 dp outline and an
 * almost-sharp 2 dp corner. Colours come from the theme, so light/dark need no
 * branching here.
 */
@Composable
fun NeoBrutalistCard(
    modifier: Modifier = Modifier,
    style: NeoBrutalistCardStyle = NeoBrutalistCardStyle.Rose,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = MaterialTheme.colorScheme
    val containerColor = when (style) {
        NeoBrutalistCardStyle.Paper -> colors.surface
        NeoBrutalistCardStyle.Rose -> colors.surfaceVariant
    }
    val outlineColor = colors.outline
    val shape = NeoBrutalistCornerShape
    val density = LocalDensity.current
    val shadowOffsetPx = with(density) { NeoBrutalistShadowOffset.toPx() }
    val cornerRadiusPx = with(density) { 2.dp.toPx() }

    Box(
        modifier = modifier
            // Reserve room for the hard shadow on the bottom/end edges.
            .padding(bottom = NeoBrutalistShadowOffset, end = NeoBrutalistShadowOffset)
            .drawBehind {
                drawRoundRect(
                    color = outlineColor,
                    topLeft = Offset(shadowOffsetPx, shadowOffsetPx),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(cornerRadiusPx),
                )
                drawRoundRect(
                    color = containerColor,
                    topLeft = Offset.Zero,
                    size = size,
                    cornerRadius = CornerRadius(cornerRadiusPx),
                )
            }
            .border(BorderStroke(NeoBrutalistBorderWidth, outlineColor), shape)
            .clip(shape)
            .padding(contentPadding),
        content = content,
    )
}
