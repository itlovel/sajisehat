package com.example.sajisehat.data.scan.remote

data class ScanNutritionResponse(
    val success: Boolean,
    val message: String?,
    val data: Data?
) {
    data class Data(
        val detections: List<DetectionDto>?,
        val image_width: Int?,
        val image_height: Int?
    )

    data class DetectionDto(
        val `class`: String,
        val confidence: Float,
        val height: Float,
        val width: Float,
        val x: Float,
        val y: Float
    )
}
