package com.example.sajisehat.feature.catalog.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ----------------- MODEL -----------------

enum class CatalogTab { PRODUCT, ARTICLE, VIDEO }

enum class ProductCategory(val label: String, val key: String) {
    PACKAGED_FOOD("Makanan Kemasan", "packaged_food"),
    PACKAGED_DRINK("Minuman Kemasan", "packaged_drink"),
    COMMON_FOOD("Makanan Umum", "common_food"),
    COMMON_DRINK("Minuman Umum", "common_drink");

    companion object {
        fun fromKey(raw: String?): ProductCategory? {
            val r = raw ?: return null
            val lower = r.lowercase()
            return values().firstOrNull {
                it.key == lower || it.name.lowercase() == lower
            }
        }
    }
}

data class CatalogProduct(
    val id: String,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val sugarGram: Double,
    val sugarLabel: String,
    val servingInfo: String,
    val category: ProductCategory
)

data class CatalogUiState(
    val tab: CatalogTab = CatalogTab.PRODUCT,

    val searchQuery: String = "",
    val selectedCategory: ProductCategory? = null,

    val allProducts: List<CatalogProduct> = emptyList(),
    val visibleProducts: List<CatalogProduct> = emptyList(),

    val bookmarkedIds: Set<String> = emptySet(),

    val loading: Boolean = true,
    val error: String? = null
)

// ----------------- VIEWMODEL -----------------

class CatalogViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(CatalogUiState())
    val state: StateFlow<CatalogUiState> = _state.asStateFlow()

    init {
        observeProducts()
        observeBookmarks()
    }

    private fun observeProducts() {
        _state.update { it.copy(loading = true, error = null) }

        db.collection("products")
            // .whereEqualTo("active", true)
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snap, e ->
                if (e != null) {
                    _state.update { it.copy(loading = false, error = e.message) }
                    return@addSnapshotListener
                }

                val list = snap?.documents?.mapNotNull { doc ->
                    val name = doc.getString("name") ?: return@mapNotNull null
                    val desc = doc.getString("description") ?: ""
                    val image = doc.getString("imageUrl")

                    val sugarLabel = doc.getString("sugarLabel") ?: ""
                    val portionLabel = doc.getString("portionLabel") ?: ""

                    val sugarNumber = Regex("(\\d+(?:[.,]\\d+)?)")
                        .find(sugarLabel)
                        ?.groupValues?.get(1)
                        ?.replace(',', '.')
                        ?.toDoubleOrNull()
                        ?: 0.0

                    val cat = ProductCategory.fromKey(doc.getString("category"))
                        ?: ProductCategory.COMMON_FOOD

                    CatalogProduct(
                        id = doc.id,
                        name = name,
                        description = desc,
                        imageUrl = image,
                        sugarGram = sugarNumber,
                        sugarLabel = sugarLabel,
                        servingInfo = portionLabel,
                        category = cat
                    )
                }.orEmpty()

                _state.update { it.copy(allProducts = list, loading = false) }
                applyFilter()
            }
    }

    private fun observeBookmarks() {
        val uid = auth.currentUser?.uid ?: return

        db.collection("users").document(uid)
            .collection("bookmarks")
            .addSnapshotListener { snap, _ ->
                val ids = snap?.documents?.map { it.id }?.toSet().orEmpty()
                _state.update { it.copy(bookmarkedIds = ids) }
            }
    }

    // ---------- Event dari UI ----------

    fun onTabSelected(tab: CatalogTab) {
        _state.update { it.copy(tab = tab) }
    }

    fun onSearchChange(query: String) {
        _state.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    fun onClearSearch() {
        _state.update { it.copy(searchQuery = "") }
        applyFilter()
    }

    fun onCategorySelected(category: ProductCategory?) {
        _state.update { it.copy(selectedCategory = category) }
        applyFilter()
    }

    fun onToggleBookmark(productId: String) {
        val uid = auth.currentUser?.uid ?: return
        val isBookmarked = _state.value.bookmarkedIds.contains(productId)
        val ref = db.collection("users").document(uid)
            .collection("bookmarks").document(productId)

        viewModelScope.launch {
            if (isBookmarked) {
                ref.delete()
            } else {
                ref.set(mapOf("createdAt" to FieldValue.serverTimestamp()))
            }
        }
    }

    // ---------- Filter lokal ----------

    private fun applyFilter() {
        val s = _state.value
        var list = s.allProducts

        s.selectedCategory?.let { cat ->
            list = list.filter { it.category == cat }
        }

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
