package com.example.sajisehat.data.home

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/* ------------ Model data (domain) ------------ */
data class Tip(
    val id: String,
    val imageUrl: String,
    val text: String
)

data class SugarSummary(
    val day: Int = 0,
    val week: Int = 0,
    val month: Int = 0
)

/* ------------ Repository API ------------ */
interface HomeRepository {
    fun tipsFlow(limit: Long = 10): Flow<List<Tip>>
    fun sugarSummaryFlow(uid: String): Flow<SugarSummary>
}

/* ------------ Firebase implementation ------------ */
class FirebaseHomeRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : HomeRepository {

    override fun tipsFlow(limit: Long): Flow<List<Tip>> = callbackFlow {
        val reg = db.collection("tips")
            .whereEqualTo("active", true)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val items = snap?.documents?.mapNotNull { d ->
                    val img = d.getString("imageUrl") ?: return@mapNotNull null
                    val text = d.getString("text") ?: return@mapNotNull null
                    Tip(d.id, img, text)
                }.orEmpty()
                trySend(items)
            }
        awaitClose { reg.remove() }
    }

    override fun sugarSummaryFlow(uid: String): Flow<SugarSummary> = callbackFlow {
        val reg = db.collection("users").document(uid)
            .collection("stats").document("consumption")
            .addSnapshotListener { snap, err ->
                if (err != null) { close(err); return@addSnapshotListener }
                val day = snap?.getLong("dayGrams")?.toInt() ?: 0
                val week = snap?.getLong("weekGrams")?.toInt() ?: 0
                val month = snap?.getLong("monthGrams")?.toInt() ?: 0
                trySend(SugarSummary(day, week, month))
            }
        awaitClose { reg.remove() }
    }
}
