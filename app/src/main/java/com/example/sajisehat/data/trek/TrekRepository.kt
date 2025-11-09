package com.example.sajisehat.data.trek

import com.example.sajisehat.data.trek.model.DailySugarData
import com.example.sajisehat.data.trek.model.TrackedProduct
import java.time.LocalDate

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

    /**
     * Total konsumsi gula (gram) untuk 1 tanggal tertentu.
     */
    suspend fun getTotalSugarForDate(
        userId: String,
        date: LocalDate
    ): Double

    /**
     * Total konsumsi gula per hari dalam rentang tanggal.
     * Mengembalikan list DailySugarData, tiap item = (tanggal, total gram).
     */
    suspend fun getTotalSugarForDateRange(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailySugarData>

    /**
     * (Untuk halaman detail nanti) – daftar item trek untuk 1 tanggal.
     */
    suspend fun getTrackedProductsForDate(
        userId: String,
        date: LocalDate
    ): List<TrackedProduct>
}
