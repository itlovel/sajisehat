package com.example.sajisehat.data.scan.model

import android.graphics.Rect
import kotlin.math.roundToInt

fun Detection.toRect(
    imageWidth: Int,
    imageHeight: Int
): Rect {
    // Asumsi Roboflow: x,y = center, width,height = size
    val left = (x - width / 2f).roundToInt()
    val top = (y - height / 2f).roundToInt()
    val right = (x + width / 2f).roundToInt()
    val bottom = (y + height / 2f).roundToInt()

    return Rect(
        left.coerceIn(0, imageWidth),
        top.coerceIn(0, imageHeight),
        right.coerceIn(0, imageWidth),
        bottom.coerceIn(0, imageHeight)
    )
}
