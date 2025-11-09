package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.sajisehat.feature.trek.ui.components.DailySugarProgressBar
import com.example.sajisehat.feature.trek.ui.components.DailySugarSummaryCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrekDetailScreen(
    state: TrekDetailUiState,
    onBack: () -> Unit,
    onDeleteItem: (String) -> Unit,
    onAddManual: () -> Unit
) {
    val darkBlue = Color(0xFF001A72)
    val yellow = Color(0xFFFFC107)

    // state untuk dialog konfirmasi hapus
    var itemToDelete by remember { mutableStateOf<TrekDetailItemUi?>(null) }

    Scaffold(
        topBar = {
            TopBarChild(
                title = "Cek Detail",
                onBack = onBack
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    shadowElevation = 4.dp,
                    color = yellow
                ) {
                    Text(
                        text = "Tambah Manual?",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FloatingActionButton(
                    onClick = onAddManual,
                    shape = CircleShape,
                    containerColor = yellow,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Tambah manual"
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {

            // Tanggal disembunyikan dari UI, tapi state.date tetap ada di data layer.

            state.todaySummary?.let { summary ->
                TodaySummaryCard(summary = summary)
                Spacer(Modifier.height(24.dp))
            }

            Text(
                text = "Riwayat Konsumsi Gula Hari Ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

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
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(state.items, key = { it.id }) { item ->
                        TrekDetailItemCard(
                            item = item,
                            borderColor = darkBlue,
                            contentColor = darkBlue,
                            onDeleteClick = { itemToDelete = item }
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

        // Dialog konfirmasi hapus
        itemToDelete?.let { selected ->
            AlertDialog(
                onDismissRequest = { itemToDelete = null },
                title = {
                    Text(
                        text = "Hapus Riwayat?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                text = {
                    Text(
                        text = "Yakin ingin menghapus riwayat konsumsi ini?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteItem(selected.id)
                            itemToDelete = null
                        }
                    ) {
                        Text(
                            text = "Hapus",
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { itemToDelete = null }
                    ) {
                        Text(text = "Batal")
                    }
                }
            )
        }
    }
}

@Composable
private fun TodaySummaryCard(summary: TodaySummaryUi) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        DailySugarSummaryCard(
            totalSugarGram = summary.totalGram,
            modifier = Modifier.fillMaxWidth()
        )

        DailySugarProgressBar(
            totalNow = summary.totalGram,
            addedSugar = 0.0,
            modifier = Modifier.fillMaxWidth()
        )
        // Text total di bawah card dihilangkan agar mirip Figma
    }
}

@Composable
private fun TrekDetailItemCard(
    item: TrekDetailItemUi,
    borderColor: Color,
    contentColor: Color,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.productName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gula: ${item.sugarGram.toInt()} gram - ${item.percentageOfDailyNeed}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Hapus",
                    tint = contentColor
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
