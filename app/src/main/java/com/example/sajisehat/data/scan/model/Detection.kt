package com.example.sajisehat.data.scan.model

data class Detection(
    val clazz: String,
    val confidence: Float,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
)
