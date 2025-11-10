package com.example.sajisehat.feature.trek.save

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import com.example.sajisehat.di.AppGraph
import com.example.sajisehat.feature.topbar.TopBarChild
import com.example.sajisehat.feature.trek.model.calculateDailyPercent
import kotlinx.coroutines.launch

@Composable
private fun DailySugarSummaryCard(
    totalSugarAfter: Double,
    modifier: Modifier = Modifier
) {
    val level = getProductSugarLevel(totalSugarAfter)
    val limitGram = 50

    val cardStrokeColor = Color(0xFFDDDDDD)          // abu tua tipis
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDFDFE),                      // hampir putih
            Color(0xFFF3F4F7)                       // abu muda ke bawah
        )
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,                      // efek 3D tipis
        shadowElevation = 3.dp,                     // bayangan halus
        border = BorderStroke(1.dp, cardStrokeColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gambar hati di kiri
            Image(
                painter = painterResource(
                    when (level) {
                        ProductSugarLevelUi.LOW -> R.drawable.ic_sugar_low
                        ProductSugarLevelUi.MEDIUM -> R.drawable.ic_sugar_medium
                        ProductSugarLevelUi.HIGH -> R.drawable.ic_sugar_high
                    }
                ),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // "Konsumsi Gula: Rendah"
                Text(
                    text = buildAnnotatedString {
                        append("Konsumsi Gula: ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002A7A)
                            )
                        ) {
                            append(level.titleText())
                        }
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )
                )

                // "Hari ini, kamu telah mengonsumsi 11 gram gula"
                Text(
                    text = buildAnnotatedString {
                        append("Hari ini, kamu telah mengonsumsi ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002A7A)
                            )
                        ) {
                            append("${"%.0f".format(totalSugarAfter)} gram")
                        }
                        append(" gula")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                )

                // Deskripsi WHO, abu dan lebih kecil
                Text(
                    text = "Tahukah kamu? WHO menyarankan bahwa standar gula harian manusia adalah tidak lebih dari $limitGram gram",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF9E9E9E)
                    )
                )
            }
        }
    }
}
@Composable
private fun DailySugarProgressBar(
    totalBefore: Double,
    addedSugar: Double,
    totalAfter: Double,
    modifier: Modifier = Modifier
) {
    val limitGram = 50.0

    val dailyPercent = calculateDailyPercent(totalAfter)
    val clampedPercent = dailyPercent.coerceIn(0, 200)

    // fraksi 0..1
    val beforeFraction = (totalBefore / limitGram).coerceIn(0.0, 1.0)
    val afterFraction = (totalAfter / limitGram).coerceIn(0.0, 1.0)
    val addedFraction = (afterFraction - beforeFraction)
        .coerceIn(0.0, 1.0 - beforeFraction)

    val cardStrokeColor = Color(0xFFDDDDDD)
    val cardBg = Brush.verticalGradient(
        listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF5F5F8)
        )
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, cardStrokeColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // ===== TEKS ATAS (pakai buildAnnotatedString) =====
            Text(
                text = buildAnnotatedString {
                    append("Konsumsi gula-mu saat ini setara dengan ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF002A7A)
                        )
                    ) {
                        append("$clampedPercent%")
                    }
                    append(" dari kebutuhan gula-mu di hari ini")
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF444444)
                )
            )

            // ===== PROGRESS BAR 3 SEGMENT (biru + kuning + abu) =====
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(50.dp))
                ) {
                    // Biru: konsumsi sebelum
                    if (beforeFraction > 0.0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(beforeFraction.toFloat())
                                .background(Color(0xFF002A7A))
                        )
                    }

                    // Kuning: tambahan produk ini
                    if (addedFraction > 0.0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(addedFraction.toFloat())
                                .background(Color(0xFFFFC107))
                        )
                    }

                    // Sisa: transparan (abu dari background)
                    val remaining = 1f - (beforeFraction + addedFraction).toFloat()
                    if (remaining > 0f) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(remaining)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "0 Gr",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF777777)
                    )
                )
                Text(
                    "${limitGram.toInt()} Gr",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF777777)
                    )
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveToTrekScreen(
    sugarGram: Double,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    initialProductName: String? = null,   // NEW: prefill dari manual
    lockProductName: Boolean = false      // NEW: field tidak bisa diubah kalau manual
) {
    val viewModel: SaveTrekViewModel = viewModel(
        factory = SaveTrekViewModelFactory(
            trekRepository = AppGraph.trekRepository,
            authRepository = AppGraph.authRepo,
            sugarGram = sugarGram
        )
    )
    val state = viewModel.uiState

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val darkBlue = Color(0xFF001A72)

    // PREFILL nama produk hanya sekali, kalau datang dari manual
    androidx.compose.runtime.LaunchedEffect(initialProductName) {
        if (initialProductName != null && state.productName.isBlank()) {
            viewModel.onProductNameChange(initialProductName)
        }
    }

    Scaffold(
        topBar = {
            TopBarChild(
                title = "Simpan Produk dalam Trek",
                onBack = onBack
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ====== SECTION JUDUL ======
            Text(
                text = "Trek Gula-mu Saat Ini",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = darkBlue
                )
            )
            Text(
                text = "Kalkulasi jumlah trek gula, jika kamu mengonsumsi produk yang ditambahkan",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF777777)
                )
            )

            // ====== CARD RINGKASAN (HATI BIRU) ======
            DailySugarSummaryCard(
                totalSugarAfter = state.totalAfter,
                modifier = Modifier.fillMaxWidth()
            )

            // ====== PROGRESS BAR ======
            DailySugarProgressBar(
                totalBefore = state.totalBefore,
                addedSugar = state.sugarGram,
                totalAfter = state.totalAfter,
                modifier = Modifier.fillMaxWidth()
            )

            // ====== INFORMASI PRODUK ======
            Text(
                text = "Informasi Produk",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "Nama Produk Yang Ditambahkan",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = state.productName,
                onValueChange = { newText ->
                    if (!lockProductName) {    // dari scan: bisa edit, dari manual: terkunci
                        viewModel.onProductNameChange(newText)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !lockProductName && !state.isLoading,
                placeholder = {
                    Text(
                        "Contoh: Sereal Coklat 30g",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFB0B0B0)
                        )
                    )
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

            // ====== BUTTON BAWAH ======
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
                        .height(40.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        "Kembali",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = {
                        viewModel.save(
                            onSuccess = onSaved,
                            onError = { message ->
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(message)
                                }
                            }
                        )
                    },
                    enabled = !state.isLoading && state.productName.isNotBlank(),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBlue,
                        disabledContainerColor = Color(0xFFB0B8D0),
                        disabledContentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (state.isLoading) "Menyimpan..." else "Simpan",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
