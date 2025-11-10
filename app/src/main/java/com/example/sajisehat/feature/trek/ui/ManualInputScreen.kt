package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onNext: () -> Unit,
    onOpenCatalog: () -> Unit
) {
    val darkBlue = Color(0xFF001A72)

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
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Judul section
            Text(
                text = "Informasi Produk",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )

            Spacer(Modifier.height(16.dp))

            // Nama Produk
            OutlinedTextField(
                value = state.productName,
                onValueChange = onProductNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = "Nama Produk*",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkBlue,
                    unfocusedBorderColor = darkBlue,
                    cursorColor = darkBlue
                )
            )

            Spacer(Modifier.height(16.dp))

            // Gula / Takaran Saji
            OutlinedTextField(
                value = state.sugarPerServingText,
                onValueChange = onSugarPerServingChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = "Gula/Takaran Saji (g)*",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkBlue,
                    unfocusedBorderColor = darkBlue,
                    cursorColor = darkBlue
                )
            )

            Spacer(Modifier.height(16.dp))

            // Takaran Saji Produk
            OutlinedTextField(
                value = state.servingSizeText,
                onValueChange = onServingSizeChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        text = "Takaran Saji Produk (g)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = darkBlue,
                    unfocusedBorderColor = darkBlue,
                    cursorColor = darkBlue
                )
            )

            Spacer(Modifier.height(24.dp))

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            // Tombol Simpan
            Button(
                onClick = onNext,
                enabled = state.isValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = darkBlue,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFBFC4D4),
                    disabledContentColor = Color.White
                )
            ) {
                Text(
                    text = "Simpan",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Atau kamu belum tahu kandungan gula dari produk yang ingin kamu simpan?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(20.dp))

            OutlinedButton(
                onClick = onOpenCatalog,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = darkBlue
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(darkBlue)
                )
            ) {
                Text(
                    text = "Cek Katalog >>",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
