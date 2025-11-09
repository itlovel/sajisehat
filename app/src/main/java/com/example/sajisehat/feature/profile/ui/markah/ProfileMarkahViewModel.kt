package com.example.sajisehat.feature.profile.ui.markah

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MarkahProduct(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val sugarLabel: String,
    val portionLabel: String
)

data class MarkahUiState(
    val searchQuery: String = "",
    val allProducts: List<MarkahProduct> = emptyList(),
    val visibleProducts: List<MarkahProduct> = emptyList(),
    val bookmarkedIds: Set<String> = emptySet(),
    val loading: Boolean = true,
    val error: String? = null
)

class ProfileMarkahViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(MarkahUiState())
    val state: StateFlow<MarkahUiState> = _state.asStateFlow()

    init {
        observeBookmarks()
        observeProducts()
    }

    private fun observeBookmarks() {
        val uid = auth.currentUser?.uid ?: run {
            _state.update { it.copy(loading = false, error = "Belum login") }
            return
        }

        db.collection("users").document(uid)
            .collection("bookmarks")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _state.update { it.copy(error = e.message, loading = false) }
                    return@addSnapshotListener
                }

                val ids = snapshot?.documents?.map { it.id }?.toSet().orEmpty()
                _state.update { it.copy(bookmarkedIds = ids, loading = false, error = null) }
                applyFilter()
            }
    }

    private fun observeProducts() {
        db.collection("products")
            .whereEqualTo("active", true)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    _state.update { it.copy(error = e.message, loading = false) }
                    return@addSnapshotListener
                }

                val list = snapshot?.documents?.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    MarkahProduct(
                        id = doc.id,
                        name = name,
                        description = doc.getString("description") ?: "",
                        imageUrl = doc.getString("imageUrl"),
                        sugarLabel = doc.getString("sugarLabel") ?: "",
                        portionLabel = doc.getString("portionLabel") ?: ""
                    )
                }.orEmpty()

                _state.update { it.copy(allProducts = list, loading = false, error = null) }
                applyFilter()
            }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun onClearSearch() {
        _state.update { it.copy(searchQuery = "") }
        applyFilter()
    }

    fun onToggleBookmark(productId: String) {
        val uid = auth.currentUser?.uid ?: return
        val isBookmarked = _state.value.bookmarkedIds.contains(productId)

        val ref = db.collection("users").document(uid)
            .collection("bookmarks")
            .document(productId)

        viewModelScope.launch {
            if (isBookmarked) {
                ref.delete()
            } else {
                // sama seperti di CatalogViewModel
                ref.set(mapOf("createdAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()))
            }
        }
    }

    private fun applyFilter() {
        val s = _state.value
        val bookmarked = s.bookmarkedIds

        var list = s.allProducts.filter { bookmarked.contains(it.id) }

        if (s.searchQuery.isNotBlank()) {
            val q = s.searchQuery.trim().lowercase()
            list = list.filter {
                it.name.lowercase().contains(q) ||
                        it.description.lowercase().contains(q)
            }
        }

        _state.update { it.copy(visibleProducts = list) }
    }
}
