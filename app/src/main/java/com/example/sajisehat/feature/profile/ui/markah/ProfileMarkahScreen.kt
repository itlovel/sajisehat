package com.example.sajisehat.feature.profile.ui.markah

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sajisehat.R
import com.example.sajisehat.feature.topbar.TopBarChild
import com.example.sajisehat.ui.theme.SajiTextStyles

@Composable
fun ProfileMarkahScreen(
    onBack: () -> Unit,
    onOpenProductDetail: (String) -> Unit = {},
    vm: ProfileMarkahViewModel = viewModel()
) {
    val st by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopBarChild(
                title = "Markah",
                onBack = onBack
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(37.dp))

            Text(
                text = "\uD83D\uDD0D Cari Produk Ber-Markah",
                style = SajiTextStyles.BodyLargeBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Telusuri informasi produk yang telah di-markah",
                style = SajiTextStyles.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            MarkahSearchField(
                value = st.searchQuery,
                onValueChange = vm::onSearchChange,
                onClear = vm::onClearSearch,
                modifier = Modifier
                    .fillMaxWidth(0.85f)              // ~90% lebar layar
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(50.dp))

            when {
                st.loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                st.error != null -> Text(
                    text = "Gagal memuat markah: ${st.error}",
                    color = MaterialTheme.colorScheme.error
                )

                st.visibleProducts.isEmpty() -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Belum ada produk di-markah.")
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(st.visibleProducts, key = { it.id }) { product ->
                            MarkahProductRow(
                                product = product,
                                bookmarked = st.bookmarkedIds.contains(product.id),
                                onClick = { onOpenProductDetail(product.id) },
                                onToggleBookmark = { vm.onToggleBookmark(product.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ------------ Search bar ------------ */

@Composable
private fun MarkahSearchField(
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

/* ------------ Item produk ber-markah ------------ */

@Composable
private fun MarkahProductRow(
    product: MarkahProduct,
    bookmarked: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
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
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Pill(
                        text = product.sugarLabel,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                    Pill(
                        text = product.portionLabel,
                        modifier = Modifier.weight(1f),
                        backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            IconButton(
                onClick = onToggleBookmark,
                modifier = Modifier.padding(start = 4.dp)
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
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Divider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
            thickness = 1.dp
        )
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
