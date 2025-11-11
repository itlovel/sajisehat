package com.example.sajisehat.feature.catalog.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sajisehat.R
import com.example.sajisehat.feature.topbar.TopBarEvent
import com.example.sajisehat.feature.topbar.TopBarViewModel
import com.example.sajisehat.ui.components.topbar.AppTopBar
import com.example.sajisehat.ui.theme.SajiTextStyles

@Composable
fun CatalogScreen(
    topBarVM: TopBarViewModel = viewModel(),
    vm: CatalogViewModel = viewModel(),
    onOpenProductDetail: (String) -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenNotification: () -> Unit = {},
    startTab: CatalogTab = CatalogTab.PRODUCT
) {
    val st by vm.state.collectAsState()

    LaunchedEffect(startTab) {
        vm.onTabSelected(startTab)
    }


    Scaffold(
        topBar = {
            AppTopBar(
                state = topBarVM.state.collectAsState().value,
                onEvent = { evt ->
                    when (evt) {
                        TopBarEvent.OnAvatarClick -> {
                            onOpenProfile()
                        }
                        TopBarEvent.OnBellClick   -> onOpenNotification()

                        else -> topBarVM.onEvent(evt)
                    }
                },
                customTitle = "Katalog",
                customSubtitle = "Eksplor Segala Informasi"
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            CatalogTabRow(
                selected = st.tab,
                onSelected = vm::onTabSelected
            )

            when (st.tab) {
                CatalogTab.PRODUCT -> ProductCatalogContent(
                    state = st,
                    onSearchChange = vm::onSearchChange,
                    onClearSearch = vm::onClearSearch,
                    onCategorySelected = vm::onCategorySelected,
                    onToggleBookmark = vm::onToggleBookmark
                )

                CatalogTab.ARTICLE -> PlaceholderTab(text = "Artikel segera hadir 👀")
                CatalogTab.VIDEO -> PlaceholderTab(text = "Video segera hadir 🎥")
            }
        }
    }
}

/* ---------- TAB ROW ATAS ---------- */

@Composable
private fun CatalogTabRow(
    selected: CatalogTab,
    onSelected: (CatalogTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        TabItem(
            text = "Katalog Produk",
            selected = selected == CatalogTab.PRODUCT,
            modifier = Modifier.weight(1f),
            onClick = { onSelected(CatalogTab.PRODUCT) }
        )
        TabItem(
            text = "Artikel",
            selected = selected == CatalogTab.ARTICLE,
            modifier = Modifier.weight(1f),
            onClick = { onSelected(CatalogTab.ARTICLE) }
        )
        TabItem(
            text = "Video",
            selected = selected == CatalogTab.VIDEO,
            modifier = Modifier.weight(1f),
            onClick = { onSelected(CatalogTab.VIDEO) }
        )
    }
    Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun TabItem(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        color = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurface,
        tonalElevation = 0.dp,
        shape = RectangleShape
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = SajiTextStyles.CaptionBold
            )
        }
    }
}

/* ---------- KONTEN TAB PRODUK ---------- */

@Composable
private fun ProductCatalogContent(
    state: CatalogUiState,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    onCategorySelected: (ProductCategory?) -> Unit,
    onToggleBookmark: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = "\uD83D\uDD0D Cari Keyword Informasi",
            style = SajiTextStyles.BodyLargeBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Cari Info Gula dari Produk yang Kamu Tahu!",
            style = SajiTextStyles.Caption,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        SearchField(
            value = state.searchQuery,
            onValueChange = onSearchChange,
            onClear = onClearSearch,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(20.dp))

        CategoryFilterRow(
            selected = state.selectedCategory,
            onCategorySelected = onCategorySelected
        )

        Spacer(Modifier.height(32.dp))

        when {
            state.loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Text(
                    text = "Gagal memuat katalog: ${state.error}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.visibleProducts.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Produk tidak ditemukan.")
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(
                        items = state.visibleProducts,
                        key = { _, it -> it.id }
                    ) { index, product ->
                        ProductItemRow(
                            product = product,
                            bookmarked = state.bookmarkedIds.contains(product.id),
                            onToggleBookmark = { onToggleBookmark(product.id) }
                        )

                        if (index < state.visibleProducts.lastIndex) {
                            Divider(
                                color = MaterialTheme.colorScheme
                                    .outlineVariant
                                    .copy(alpha = 0.6f),
                                thickness = 1.dp
                            )
                        }
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
    }
}

/* ---------- SEARCH FIELD ---------- */

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = MaterialTheme.colorScheme.primary

    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        color = Color.White,
        tonalElevation = 0.dp,
        modifier = modifier
            .height(44.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = SajiTextStyles.Body.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            "Cari",
                            style = SajiTextStyles.Body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    inner()
                }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (value.isNotBlank()) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Bersihkan",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                }
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = "Search",
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/* ---------- FILTER KATEGORI ---------- */

@Composable
private fun CategoryFilterRow(
    selected: ProductCategory?,
    onCategorySelected: (ProductCategory?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            CategoryChip(
                text = ProductCategory.PACKAGED_FOOD.label,
                selected = selected == ProductCategory.PACKAGED_FOOD,
                onClick = {
                    onCategorySelected(
                        if (selected == ProductCategory.PACKAGED_FOOD) null
                        else ProductCategory.PACKAGED_FOOD
                    )
                }
            )
            CategoryChip(
                text = ProductCategory.PACKAGED_DRINK.label,
                selected = selected == ProductCategory.PACKAGED_DRINK,
                onClick = {
                    onCategorySelected(
                        if (selected == ProductCategory.PACKAGED_DRINK) null
                        else ProductCategory.PACKAGED_DRINK
                    )
                }
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            CategoryChip(
                text = ProductCategory.COMMON_FOOD.label,
                selected = selected == ProductCategory.COMMON_FOOD,
                onClick = {
                    onCategorySelected(
                        if (selected == ProductCategory.COMMON_FOOD) null
                        else ProductCategory.COMMON_FOOD
                    )
                }
            )
            CategoryChip(
                text = ProductCategory.COMMON_DRINK.label,
                selected = selected == ProductCategory.COMMON_DRINK,
                onClick = {
                    onCategorySelected(
                        if (selected == ProductCategory.COMMON_DRINK) null
                        else ProductCategory.COMMON_DRINK
                    )
                }
            )
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)

    Surface(
        onClick = onClick,
        modifier = modifier.height(32.dp),
        shape = RoundedCornerShape(8.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        border = if (selected) null else border,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 4.dp)
                )
            }
            Text(
                text = text,
                style = SajiTextStyles.CaptionBold,
                color = if (selected) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.primary
            )
        }
    }
}

/* ---------- ITEM PRODUK ---------- */

@Composable
private fun ProductItemRow(
    product: CatalogProduct,
    bookmarked: Boolean,
    onToggleBookmark: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                tonalElevation = 0.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(72.dp)
            ) {
                if (product.imageUrl.isNullOrBlank()) {
                    Icon(
                        painter = painterResource(R.drawable.ic_product_placeholder),
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = SajiTextStyles.BodyBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = product.description,
                    style = SajiTextStyles.Caption,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Pill(
                        text = product.sugarLabel,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Pill(
                        text = product.servingInfo,
                        modifier = Modifier.weight(1f),
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(25.dp)
                ) {
                    val iconRes = if (bookmarked) {
                        R.drawable.ic_bookmark_filled
                    } else {
                        R.drawable.ic_bookmark_border
                    }
                    val tintColor = if (bookmarked) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = "Markah",
                        tint = tintColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun Pill(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = modifier.height(24.dp),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = text,
                style = SajiTextStyles.Caption,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/* ---------- PLACEHOLDER UNTUK TAB LAIN ---------- */

@Composable
private fun PlaceholderTab(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}
