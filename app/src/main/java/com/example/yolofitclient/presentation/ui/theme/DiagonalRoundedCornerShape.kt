package com.example.yolofitclient.presentation.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection


class DiagonalRoundedCornerShape(
    private val topLeft: Float = 0f,
    private val topRight: Float = 0f,
    private val bottomRight: Float = 0f,
    private val bottomLeft: Float = 0f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(topLeft, 0f)
            lineTo(size.width - topRight, 0f)
            cubicTo(
                size.width - topRight * 0.3f, 0f,
                size.width, topRight * 0.3f,
                size.width, topRight
            )
            lineTo(size.width, size.height - bottomRight)
            cubicTo(
                size.width, size.height - bottomRight * 0.3f,
                size.width - bottomRight * 0.3f, size.height,
                size.width - bottomRight, size.height
            )
            lineTo(bottomLeft, size.height)
            cubicTo(
                bottomLeft * 0.3f, size.height,
                0f, size.height - bottomLeft * 0.3f,
                0f, size.height - bottomLeft
            )
            lineTo(0f, topLeft)
            cubicTo(
                0f, topLeft * 0.3f,
                topLeft * 0.3f, 0f,
                topLeft, 0f
            )
            close()
        }
        return Outline.Generic(path)
    }
}