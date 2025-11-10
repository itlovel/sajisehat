package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sajisehat.feature.scan.SugarSummaryCard
import com.example.sajisehat.feature.scan.SugarLevel
import com.example.sajisehat.feature.trek.model.ManualResultUi
import com.example.sajisehat.feature.trek.model.SugarLevelUi
import com.example.sajisehat.feature.topbar.TopBarChild

@Composable
fun ManualCalcScreen(
    result: ManualResultUi,
    onBack: () -> Unit,
    onAddToTrek: () -> Unit
) {
    val darkBlue = Color(0xFF001A72)

    // mapping SugarLevelUi -> SugarLevel (punyanya modul scan)
    val scanLevel = when (result.level) {
        SugarLevelUi.LOW -> SugarLevel.RENDAH
        SugarLevelUi.MEDIUM -> SugarLevel.SEDANG
        SugarLevelUi.HIGH -> SugarLevel.TINGGI
        SugarLevelUi.UNKNOWN -> SugarLevel.SEDANG
    }

    Scaffold(
        topBar = {
            TopBarChild(
                title = "Hasil Kalkulasi",
                onBack = onBack
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ========= JUDUL (sama gaya dengan ScanResultSection) =========
            Text(
                text = "Hasil Kalkulasi Produk:",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 4.dp)
                    .fillMaxWidth()
            )

            // ========= KARTU UTAMA: SugarSummaryCard =========
            SugarSummaryCard(
                level = scanLevel,
                sugarPerServing = result.sugarPerServingGram,
                dailyPercent = result.percentOfDailyNeed,
                modifier = Modifier.fillMaxWidth()
            )

            // ========= KALKULASI PER 1x TAKARAN SAJI (mirip ScanResultSection) =========
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

                    result.servingSizeGram?.let {
                        InfoRowManual(
                            label = "Takaran per 1x saji:",
                            value = "${it.toInt()} Gram"
                        )
                    }

                    InfoRowManual(
                        label = "Kandungan gula per 1x saji:",
                        value = "${result.sugarPerServingGram.toInt()} Gram"
                    )

                    InfoRowManual(
                        label = "% terhadap kebutuhan gula/hari:",
                        value = "${result.percentOfDailyNeed} %"
                    )
                }
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

            // ========= BUTTON BAWAH: Kembali & Tambah =========
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = darkBlue
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.dp,
                        brush = androidx.compose.ui.graphics.SolidColor(darkBlue)
                    )
                ) {
                    Text(
                        text = "Kembali",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Button(
                    onClick = onAddToTrek,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp)
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

// ========== ROW INFO KECIL (copy gaya InfoRow di ScanResultSection) ==========
@Composable
private fun InfoRowManual(
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

private fun SugarLevelUi.toDisplayText(): String = when (this) {
    SugarLevelUi.LOW -> "Rendah"
    SugarLevelUi.MEDIUM -> "Sedang"
    SugarLevelUi.HIGH -> "Tinggi"
    SugarLevelUi.UNKNOWN -> "-"
}
