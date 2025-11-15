package com.example.sajisehat.data.scan.remote

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class ScanNutritionRemoteDataSource(
    private val api: ScanNutritionApiService
) {
    suspend fun detectLayout(imageBytes: ByteArray): ScanNutritionResponse {
        val requestBody = imageBytes.toRequestBody("image/jpeg".toMediaType())
        val part = MultipartBody.Part.createFormData(
            name = "image",      // IMPORTANT: harus sama dengan key di Flask: request.files["image"]
            filename = "label.jpg",
            body = requestBody
        )
        return api.scanNutrition(part)
    }
}