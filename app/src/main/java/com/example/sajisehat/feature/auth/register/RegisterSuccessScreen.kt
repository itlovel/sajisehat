package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sajisehat.R
import kotlinx.coroutines.delay

@Composable
fun RegisterSuccessScreen(onGoHome: () -> Unit) {
    // ✅ Auto-redirect ke Home setelah 1.2 detik
    LaunchedEffect(Unit) {
        delay(1200)
        onGoHome()
    }

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(R.drawable.ic_app_logo_large),
                contentDescription = null,
                modifier = Modifier.size(160.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "SAJISEHAT",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
                )
                Spacer(Modifier.height(16.dp))
                Text("buat akun", style = MaterialTheme.typography.bodyMedium)
                Text(
                    "sukses",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                )
            }
            Column(Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = 1f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                Text("100%", style = MaterialTheme.typography.labelSmall)
            }

            // Fallback (opsional): jika user ingin langsung ketuk
            TextButton(onClick = onGoHome) {
                Text("Ke Beranda sekarang")
            }
        }
    }
}
