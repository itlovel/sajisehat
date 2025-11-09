package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sajisehat.feature.topbar.TopBarChild
import com.example.sajisehat.feature.trek.model.TrekDetailItemUi
import com.example.sajisehat.feature.trek.model.TrekDetailUiState
import com.example.sajisehat.feature.trek.model.SugarLevelUi
import com.example.sajisehat.feature.trek.model.TodaySummaryUi
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrekDetailScreen(
    state: TrekDetailUiState,
    onBack: () -> Unit,
    onDeleteItem: (String) -> Unit,
    onAddManual: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarChild(
                title = "Cek Detail",
                onBack = onBack
            )
        },
        floatingActionButton = {
            // FAB "Tambah Manual?"
            ExtendedFloatingActionButton(
                onClick = onAddManual,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Tambah manual"
                    )
                },
                text = {
                    Text(text = "Tambah Manual?")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Tanggal yang sedang dilihat
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale("id"))
            Text(
                text = "Tanggal: ${state.date.format(formatter)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            state.todaySummary?.let { summary ->
                TodaySummaryCard(summary = summary)
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Riwayat Konsumsi Gula Hari Ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.items.isEmpty()) {
                Text(
                    text = "Belum ada konsumsi yang tercatat hari ini.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        TrekDetailItemCard(
                            item = item,
                            onDelete = { onDeleteItem(item.id) }
                        )
                    }
                }
            }

            state.errorMessage?.let { msg ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = msg,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
private fun TodaySummaryCard(summary: TodaySummaryUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Konsumsi Gula Hari Ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Status: ${summary.level.toDisplayText()}",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Total: ${summary.totalGram.toInt()} gram (${summary.percentageOfNeed}%)",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun TrekDetailItemCard(
    item: TrekDetailItemUi,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Gula: ${item.sugarGram.toInt()} gram - ${item.percentageOfDailyNeed}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = Color.Gray
                )
            }
        }
    }
}

private fun SugarLevelUi.toDisplayText(): String = when (this) {
    SugarLevelUi.LOW -> "Rendah"
    SugarLevelUi.MEDIUM -> "Sedang"
    SugarLevelUi.HIGH -> "Tinggi"
    SugarLevelUi.UNKNOWN -> "-"
}
