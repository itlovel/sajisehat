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
import com.example.sajisehat.feature.topbar.TopBarChild

@Composable
fun ScanResultSection(
    result: ScanResultUi?,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onScanAgain: () -> Unit,
    onBack: () -> Unit,
    onSaveToTrek: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (result == null) {
        Column(
            modifier = modifier.fillMaxSize()
        ) {
            TopBarChild(
                title = "Scan Label",
                onBack = onBack
            )

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
        TopBarChild(
            title = "Scan Label",
            onBack = onBack
        )

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
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .fillMaxWidth()
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
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        )
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
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
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
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFB0B0B0),
                        fontWeight = FontWeight.Medium
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
                style = MaterialTheme.typography.bodySmall.copy(
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
                        .height(40.dp)
                ) {
                    Text(
                        text = "Scan Lagi",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onSaveToTrek,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Text(
                        text = "Tambah",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
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
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF555555)
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.End
        )
    }
}
