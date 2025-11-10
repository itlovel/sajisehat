package com.example.sajisehat.feature.trek.save

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.sajisehat.R

@Composable
fun SaveTrekSuccessScreen(
    onGoToTrek: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF6F7FB) // sedikit abu muda biar nggak putih polos banget
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Icon lingkaran centang + sparkles
            Image(
                painter = painterResource(R.drawable.ic_trek_success), // ganti dengan assetmu
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Judul
            Text(
                text = "Produk Berhasil Ditambahkan!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF000000)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subjudul
            Text(
                text = "Cek di Trek Gula-Mu",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF777777)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Pergi Ke Trek Gula (outlined, stroke biru, teks kecil)
            OutlinedButton(
                onClick = onGoToTrek,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color(0xFF002A7A)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFF002A7A)
                )
            ) {
                Text(
                    text = "Pergi Ke Trek Gula",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
