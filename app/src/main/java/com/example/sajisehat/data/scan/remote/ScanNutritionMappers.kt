package com.example.sajisehat.data.scan.remote

import com.example.sajisehat.data.scan.model.Detection

data class LayoutDetectionResult(
    val detections: List<Detection>,
    val imageWidth: Int,
    val imageHeight: Int
)

fun ScanNutritionResponse.toLayoutDetectionResult(): LayoutDetectionResult {
    val d = data

    val detections = d?.detections?.map { dto ->
        Detection(
            clazz = dto.`class`,
            confidence = dto.confidence,
            x = dto.x,
            y = dto.y,
            width = dto.width,
            height = dto.height
        )
    } ?: emptyList()

    val w = d?.image_width ?: 0
    val h = d?.image_height ?: 0

    return LayoutDetectionResult(
        detections = detections,
        imageWidth = w,
        imageHeight = h
    )
}
