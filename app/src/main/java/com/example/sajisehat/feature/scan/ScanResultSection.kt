package com.example.sajisehat.feature.scan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sajisehat.R

@Composable
fun ScanResultSection(
    result: ScanResultUi?,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onScanAgain: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            ResultTopAppBar(onBack = onBack)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Belum ada hasil scan",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        return
    }

    val sugarPerServing = result.sugarPerServingGram ?: 0.0
    val dailyPercent = result.dailyPercent ?: 0

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // ========= APP BAR =========
        ResultTopAppBar(onBack = onBack)

        // ========= BODY =========
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // JUDUL BESAR
            Text(
                text = "Hasil Kalkulasi Scan Label Kamu:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )

            // KARTU UTAMA (gradient biru + gif)
            SugarSummaryCard(
                level = result.sugarLevel ?: SugarLevel.RENDAH,
                sugarPerServing = sugarPerServing,
                dailyPercent = dailyPercent,
                modifier = Modifier.fillMaxWidth()
            )

            // ========= KALKULASI PER 1x TAKARAN SAJI =========
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9F9FB),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Kalkulasi per 1x Takaran Saji",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    InfoRow(
                        label = "Takaran per 1x saji:",
                        value = result.servingSizeGram?.let { "%.0f Gram".format(it) } ?: "-"
                    )
                    InfoRow(
                        label = "Kandungan gula per 1x saji:",
                        value = "%.1f Gram".format(sugarPerServing)
                    )
                    InfoRow(
                        label = "% terhadap kebutuhan gula/hari:",
                        value = "%d %%".format(dailyPercent)
                    )
                }
            }

            // ========= INFORMASI BERDASARKAN PRODUK (EXPAND/COLLAPSE) =========
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF9F9FB),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Informasi Berdasarkan Produk",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            InfoRow(
                                label = "Jumlah sajian per kemasan:",
                                value = result.servingsPerPack?.toString() ?: "-"
                            )
                            InfoRow(
                                label = "Takaran saji produk:",
                                value = result.servingSizeGram?.let { "%.0f Gram".format(it) } ?: "-"
                            )
                            InfoRow(
                                label = "Kandungan gula per kemasan:",
                                value = result.sugarPerPackGram?.let { "%.1f Gram".format(it) } ?: "-"
                            )
                        }
                    }
                }
            }

            // ========= TEKS + ICON "TAMPILKAN INFO" =========
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpanded)
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isExpanded)
                        "Tampilkan Lebih Sedikit Informasi"
                    else
                        "Tampilkan Lebih Banyak Informasi",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color(0xFFB0B0B0)
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = if (isExpanded)
                        Icons.Filled.KeyboardDoubleArrowUp
                    else
                        Icons.Filled.KeyboardDoubleArrowDown,
                    contentDescription = null,
                    tint = Color(0xFFB0B0B0),
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // ========= TEXT KECIL DI ATAS BUTTON =========
            Text(
                text = "Ingin tambahkan produk ke track gula kamu hari ini?",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF555555)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // ========= BUTTON BAWAH: Scan Lagi & Tambah =========
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onScanAgain,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Scan Lagi",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = { /* TODO: navigate ke layar simpan trek + input nama produk */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Tambah",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ========== APP BAR DENGAN SUDUT BAWAH ROUNDED ==========
@Composable
private fun ResultTopAppBar(
    onBack: () -> Unit
) {
    Surface(
        color = Color(0xFF002A7A),
        shape = RoundedCornerShape(
            bottomStart = 16.dp,
            bottomEnd = 16.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(R.drawable.ic_back), // ganti dengan icon back-mu
                    contentDescription = "Kembali",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Scan Label",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

// ========== ROW INFO KECIL ==========
@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.End
        )
    }
}
