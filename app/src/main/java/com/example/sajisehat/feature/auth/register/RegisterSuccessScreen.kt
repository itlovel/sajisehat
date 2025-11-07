package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sajisehat.R

@Composable
fun RegisterSuccessScreen(onGoHome: () -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(painter = painterResource(R.drawable.ic_app_logo_large), contentDescription = null, modifier = Modifier.size(160.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("SAJISEHAT", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold))
                Spacer(Modifier.height(16.dp))
                Text("buat akun", style = MaterialTheme.typography.bodyMedium)
                Text("sukses", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
            }
            Column(Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(6.dp))
                Text("100%", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
    // opsional: navigate otomatis setelah delay
    // LaunchedEffect(Unit) { delay(1000); onGoHome() }
}
