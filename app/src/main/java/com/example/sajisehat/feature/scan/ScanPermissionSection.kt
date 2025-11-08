package com.example.sajisehat.feature.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ScanPermissionSection(
    permissionStatus: CameraPermissionStatus,
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Izinkan Akses Kamera",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Untuk membaca label gizi dari kemasan, aplikasi membutuhkan akses kamera.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                if (permissionStatus == CameraPermissionStatus.PERMANENTLY_DENIED) {
                    Text(
                        text = "Izin kamera sebelumnya ditolak permanen. Silakan aktifkan kembali dari Pengaturan aplikasi.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { /* misalnya: navController.popBackStack() nanti */ },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Nanti Saja")
                    }

                    Button(
                        onClick = onGrantClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Izinkan")
                    }
                }
            }
        }
    }
}
