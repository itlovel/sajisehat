package com.example.sajisehat.feature.trek.save

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import com.example.sajisehat.di.AppGraph

@Composable
private fun DailySugarSummaryCard(
    totalSugarAfter: Double,
    modifier: Modifier = Modifier
) {
    val level = getSugarLevelUi(totalSugarAfter)
    val dailyPercent = calculateDailyPercent(totalSugarAfter).coerceAtMost(200) // biar ga kepanjangan
    val limitGram = 50

    androidx.compose.material3.Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F6FF)) // background card
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar hati di kiri
            Image(
                painter = painterResource(
                    when (level) {
                        SugarLevelUi.LOW -> R.drawable.ic_sugar_low    // ganti dengan assetmu
                        SugarLevelUi.MEDIUM -> R.drawable.ic_sugar_medium
                        SugarLevelUi.HIGH -> R.drawable.ic_sugar_high
                    }
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Konsumsi Gula: ${level.titleText()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color(0xFF001C54)
                )

                Text(
                    text = "Hari ini, kamu telah mengonsumsi ${"%.0f".format(totalSugarAfter)} gram gula",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = "Tahukah kamu? WHO menyarankan bahwa standar gula harian manusia adalah tidak lebih dari $limitGram gram",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF777777)
                )
            }
        }
    }
}

@Composable
private fun DailySugarProgressBar(
    totalSugarAfter: Double,
    modifier: Modifier = Modifier
) {
    val dailyPercent = calculateDailyPercent(totalSugarAfter)
    val clampedPercent = dailyPercent.coerceIn(0, 200) // biar ga kelewatan
    val limitGram = 50

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF9F9FB),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Konsumsi gula-mu saat ini setara dengan $clampedPercent% dari kebutuhan gula-mu di hari ini",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(4.dp))

            // fake progressbar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(
                            (clampedPercent / 100f).coerceAtMost(1f)
                        )
                        .background(Color(0xFFFFC107), RoundedCornerShape(50))
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0 Gr", style = MaterialTheme.typography.labelSmall)
                Text("$limitGram Gr", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveToTrekScreen(
    sugarGram: Double,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: SaveTrekViewModel = viewModel(
        factory = SaveTrekViewModelFactory(
            trekRepository = AppGraph.trekRepository,
            authRepository = AppGraph.authRepo,
            sugarGram = sugarGram
        )
    )
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Simpan Produk dalam Trek",
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF002A7A)   // biru tua figma
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ====== Teks judul section ======
            Text(
                text = "Trek Gula-mu Saat Ini",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001C54)
                )
            )
            Text(
                text = "Kalkulasi jumlah trek gula, jika kamu mengonsumsi produk yang ditambahkan",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF777777)
            )

            // ====== Kartu utama ala Figma ======
            DailySugarSummaryCard(
                totalSugarAfter = state.totalAfter,
                modifier = Modifier.fillMaxWidth()
            )

            // ====== progress bar ala Figma ======
            DailySugarProgressBar(
                totalSugarAfter = state.totalAfter,
                modifier = Modifier.fillMaxWidth()
            )

            Divider(Modifier.padding(top = 4.dp))

            // ====== Informasi Produk ======
            Text(
                text = "Informasi Produk",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Beri tahu kami nama produk yang baru Anda Scan:",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = state.productName,
                onValueChange = { viewModel.onProductNameChange(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !state.isLoading,
                placeholder = {
                    Text("Contoh: Sereal Coklat 30g")
                }
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.weight(1f))

            // ====== Tombol bawah (Kembali & Simpan) ======
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    enabled = !state.isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Kembali")
                }

                Button(
                    onClick = {
                        viewModel.save(
                            onSuccess = onSaved,
                            onError = { /* TODO snackbar */ }
                        )
                    },
                    enabled = !state.isLoading && state.productName.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF002A7A),
                        disabledContainerColor = Color(0xFFB0B8D0)
                    )
                ) {
                    Text(
                        text = if (state.isLoading) "Menyimpan..." else "Simpan",
                        color = Color.White
                    )
                }
            }
        }
    }
}