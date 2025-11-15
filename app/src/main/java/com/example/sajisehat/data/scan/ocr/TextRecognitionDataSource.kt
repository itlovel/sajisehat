package com.example.sajisehat.data.scan.ocr

import android.graphics.Bitmap

interface TextRecognitionDataSource {
    suspend fun recognizeText(bitmap: Bitmap): String
}
