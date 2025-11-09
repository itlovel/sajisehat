package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sajisehat.feature.trek.model.ManualInputUiState
import com.example.sajisehat.feature.topbar.TopBarChild

@Composable
fun ManualInputScreen(
    state: ManualInputUiState,
    onBack: () -> Unit,
    onProductNameChange: (String) -> Unit,
    onSugarPerServingChange: (String) -> Unit,
    onServingSizeChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarChild(
                title = "Tambah Produk dalam Trek",
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
            Text(text = "Informasi Produk")

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = state.productName,
                onValueChange = onProductNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Nama Produk*") }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.sugarPerServingText,
                onValueChange = onSugarPerServingChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Gula/Takaran Saji (g)*") }
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = state.servingSizeText,
                onValueChange = onServingSizeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Takaran Saji Produk (g)") }
            )

            Spacer(Modifier.height(24.dp))

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = androidx.compose.ui.graphics.Color.Red
                )
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = onNext,
                enabled = state.isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Simpan")
            }

            Spacer(Modifier.height(8.dp))

            // nanti di step berikut kita tambahkan tombol "Cek Katalog >>"
        }
    }
}
