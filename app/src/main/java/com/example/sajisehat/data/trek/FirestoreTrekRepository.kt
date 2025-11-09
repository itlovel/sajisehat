package com.example.sajisehat.data.trek

import com.example.sajisehat.data.trek.model.DailySugarData
import com.example.sajisehat.data.trek.model.TrackedProduct
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class FirestoreTrekRepository(
    private val firestore: FirebaseFirestore
) : TrekRepository {


    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    private fun trekCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("trek")

    // ====== FUNGSI BARU STEP 2 ======

    override suspend fun getTotalSugarForDate(
        userId: String,
        date: LocalDate
    ): Double {
        val dateString = date.format(dateFormatter)

        val snapshot = trekCollection(userId)
            .whereEqualTo("date", dateString)
            .get()
            .await()

        return snapshot.documents
            .mapNotNull { it.getDouble("sugarGram") }
            .sum()
    }

    override suspend fun getTotalSugarForDateRange(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<DailySugarData> {
        val startString = startDate.format(dateFormatter)
        val endString = endDate.format(dateFormatter)

        val snapshot = trekCollection(userId)
            .whereGreaterThanOrEqualTo("date", startString)
            .whereLessThanOrEqualTo("date", endString)
            .get()
            .await()

        // groupBy tanggal string
        val grouped = snapshot.documents.groupBy { doc ->
            doc.getString("date") ?: ""
        }

        return grouped.mapNotNull { (dateStr, docs) ->
            if (dateStr.isBlank()) return@mapNotNull null

            val total = docs
                .mapNotNull { it.getDouble("sugarGram") }
                .sum()

            val localDate = LocalDate.parse(dateStr, dateFormatter)
            DailySugarData(
                date = localDate,
                totalGram = total
            )
        }
    }

    override suspend fun getTrackedProductsForDate(
        userId: String,
        date: LocalDate
    ): List<TrackedProduct> {
        val dateString = date.format(dateFormatter)

        val snapshot = trekCollection(userId)
            .whereEqualTo("date", dateString)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(TrackedProduct::class.java)?.copy(
                id = doc.id  // pakai doc.id sebagai id
            )
        }
    }


    override suspend fun addTrackedProduct(entry: TrackedProduct) {
        val userId = entry.userId
        require(userId.isNotBlank()) { "userId tidak boleh kosong saat menyimpan trek" }

        val docId = if (entry.id.isBlank()) UUID.randomUUID().toString() else entry.id

        // path: users/{userId}/trek/{docId}
        val docRef = firestore
            .collection("users")
            .document(userId)
            .collection("trek")
            .document(docId)

        val data = entry.copy(id = docId)

        docRef.set(data).await()
    }

    override suspend fun getTodayTotalSugar(userId: String, date: String): Double {
        if (userId.isBlank() || date.isBlank()) return 0.0

        val snapshot = firestore
            .collection("users")
            .document(userId)
            .collection("trek")
            .whereEqualTo("date", date)
            .get()
            .await()

        return snapshot.documents.sumOf { doc ->
            (doc.getDouble("sugarGram") ?: 0.0)
        }
    }
}
