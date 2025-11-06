package com.example.sajisehat.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.feature.topbar.TopBarViewModel
import com.example.sajisehat.ui.components.topbar.AppTopBar

@Composable
fun HomeScreen(
    onOpen: (String) -> Unit = {},
    topBarViewModel: TopBarViewModel = viewModel()
) {
    val topBarState by topBarViewModel.state.collectAsState()

    androidx.compose.material3.Scaffold(
        topBar = {
            // ✅ Top App Bar dipakai di sini
            AppTopBar(
                state = topBarState,
                onEvent = topBarViewModel::onEvent
            )
        }
        // ❌ BottomBar JANGAN ditaruh di sini — sudah dipasang di App.kt (SajisehatApp)
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
//            Spacer(Modifier.height(8.dp))
//
//            // --- Tip of The Day (placeholder) ---
//            Card(
//                colors = CardDefaults.cardColors(
//                    containerColor = MaterialTheme.colorScheme.secondaryContainer
//                        .copy(alpha = 0.35f) // kesan “krem” lembut
//                ),
//                shape = RoundedCornerShape(16.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(Modifier.padding(16.dp)) {
//                    Text(
//                        text = "\uD83D\uDC40 Tip Of The Day",
//                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
//                        color = MaterialTheme.colorScheme.onSecondaryContainer
//                    )
//                    Spacer(Modifier.height(8.dp))
//                    Text(
//                        text = "Batasi minuman manis jadi hanya 1 gelas sehari untuk menjaga gula darah tetap stabil!",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onSecondaryContainer
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(16.dp))
//
//            // --- Kategori (placeholder) ---
//            Text(
//                text = "Kategori:",
//                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
//                color = MaterialTheme.colorScheme.onBackground
//            )
//            Spacer(Modifier.height(8.dp))
//            Card(
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
//                shape = RoundedCornerShape(16.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Row(
//                    modifier = Modifier.padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Pill("Tambah Manual")
//                    Pill("Baca Artikel")
//                    Pill("Nonton Video")
//                }
//            }
//
//            Spacer(Modifier.height(16.dp))
//
//            // --- Minggu Ini (placeholder) ---
//            Card(
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
//                shape = RoundedCornerShape(16.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Column(Modifier.padding(16.dp)) {
//                    Text(
//                        text = "MINGGU INI",
//                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                    Spacer(Modifier.height(4.dp))
//                    Text(
//                        text = "Konsumsi Gulamu:",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Spacer(Modifier.height(12.dp))
//                    Text(
//                        text = "70",
//                        style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
//                        color = MaterialTheme.colorScheme.onSurface
//                    )
//                    Spacer(Modifier.height(6.dp))
//                    Text(
//                        text = "Gram Gula",
//                        style = MaterialTheme.typography.labelLarge,
//                        color = MaterialTheme.colorScheme.secondary
//                    )
//                    Spacer(Modifier.height(6.dp))
//                    Text(
//                        text = "Catatan: Waduh, hampir mencapai batas maksimum asupan gula nih!",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
//                    )
//                }
//            }
//
//            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Pill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White
        )
    }
}
