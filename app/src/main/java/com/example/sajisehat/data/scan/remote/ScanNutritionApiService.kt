package com.example.sajisehat.data.scan.remote

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ScanNutritionApiService {

    @Multipart
    @POST("scan-nutrition")
    suspend fun scanNutrition(
        @Part image: MultipartBody.Part
    ): ScanNutritionResponse
}
