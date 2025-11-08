package com.example.sajisehat.data.trek

import com.example.sajisehat.data.trek.model.TrackedProduct
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreTrekRepository(
    private val firestore: FirebaseFirestore
) : TrekRepository {

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
