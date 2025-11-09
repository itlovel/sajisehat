package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sajisehat.feature.trek.model.ManualResultUi
import com.example.sajisehat.feature.trek.model.SugarLevelUi
import com.example.sajisehat.feature.topbar.TopBarChild
import androidx.compose.ui.graphics.Color

@Composable
fun ManualCalcScreen(
    result: ManualResultUi,
    onBack: () -> Unit,
    onAddToTrek: () -> Unit
) {
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            Text(
                text = "Hasil Kalkulasi Produk:",
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            // Card biru seperti desain
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Kandungan Gula: ${result.level.toDisplayText()}",
                        style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Produk mengandung ${result.sugarPerServingGram.toInt()} gram gula per 1 takaran saji-nya",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${result.percentOfDailyNeed}% dari kebutuhan gulamu per hari",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFEE8A00)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Kalkulasi per 1x Takaran Saji",
                style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                result.servingSizeGram?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Takaran per 1x saji:")
                        Text("${it.toInt()} Gram")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Kandungan gula per 1x saji:")
                    Text("${result.sugarPerServingGram.toInt()} Gram")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("% terhadap kebutuhan gula/hari:")
                    Text("${result.percentOfDailyNeed} %")
                }
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Kembali")
                }

                Button(
                    onClick = onAddToTrek,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Tambah")
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun SugarLevelUi.toDisplayText(): String = when (this) {
    SugarLevelUi.LOW -> "Rendah"
    SugarLevelUi.MEDIUM -> "Sedang"
    SugarLevelUi.HIGH -> "Sedang" // atau "Tinggi" kalau desainmu pakai itu
    SugarLevelUi.UNKNOWN -> "-"
}
