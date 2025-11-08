// feature/scan/ScanResultSection.kt
package com.example.sajisehat.feature.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScanResultSection(
    result: ScanResultUi?,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    onScanAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (result == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Belum ada hasil scan.")
            }
            return@Surface
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Hasil Kalkulasi Scan Label Kamu",
                style = MaterialTheme.typography.titleMedium
            )

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = when (result.sugarLevel) {
                            SugarLevel.RENDAH -> "Kandungan gula: Rendah"
                            SugarLevel.SEDANG -> "Kandungan gula: Sedang"
                            SugarLevel.TINGGI -> "Kandungan gula: Tinggi"
                            else -> "Kandungan gula"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    result.dailyPercent?.let {
                        Text(
                            text = "Per 1x takaran saji ≈ $it% dari kebutuhan gula harian",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Divider()

                    Text(
                        text = "Kalkulasi per 1x Takaran Saji",
                        style = MaterialTheme.typography.labelLarge
                    )

                    Text(
                        text = buildString {
                            append("Gula per 1 saji: ")
                            append(result.sugarPerServingGram?.let { "${"%.1f".format(it)} g" } ?: "-")
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    result.servingSizeGram?.let {
                        Text(
                            text = "Takaran saji: ${"%.0f".format(it)} g",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            OutlinedButton(onClick = onToggleExpanded) {
                Text(if (isExpanded) "Sembunyikan info produk" else "Tampilkan info lebih banyak")
            }

            if (isExpanded) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Informasi Berdasarkan Produk",
                            style = MaterialTheme.typography.labelLarge
                        )

                        result.productName?.let {
                            Text("Produk: $it", style = MaterialTheme.typography.bodyMedium)
                        }

                        Text(
                            text = "Jumlah sajian per kemasan: " +
                                    (result.servingsPerPack?.toString() ?: "-"),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        result.sugarPerPackGram?.let {
                            Text(
                                text = "Gula total per kemasan: ${"%.1f".format(it)} g",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onScanAgain,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Scan Lagi")
                }

                Button(
                    onClick = {
                        // TODO: dihubungkan ke fitur Trek Gula (tambah ke tracking)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tambah")
                }
            }
        }
    }
}
