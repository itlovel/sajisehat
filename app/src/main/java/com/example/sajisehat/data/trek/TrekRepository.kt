package com.example.sajisehat.data.trek

import com.example.sajisehat.data.trek.model.TrackedProduct

interface TrekRepository {

    /**
     * Simpan satu produk hasil scan ke Firestore.
     */
    suspend fun addTrackedProduct(entry: TrackedProduct)

    /**
     * Hitung total gram gula untuk user tertentu pada tanggal tertentu.
     * Tanggal pakai format "yyyy-MM-dd".
     */
    suspend fun getTodayTotalSugar(userId: String, date: String): Double
}
