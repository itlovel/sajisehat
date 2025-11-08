package com.example.sajisehat.feature.trek.save

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.di.AppGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveToTrekScreen(
    sugarGram: Double,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val viewModel: SaveTrekViewModel = viewModel(
        factory = SaveTrekViewModelFactory(
            trekRepository = AppGraph.trekRepository,
            authRepository = AppGraph.authRepo,
            sugarGram = sugarGram
        )
    )

    var state by remember { mutableStateOf(viewModel.uiState) }

    // sync perubahan uiState dari ViewModel
    LaunchedEffect(Unit) {
        snapshotFlow { viewModel.uiState }.collect { state = it }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Simpan Produk dalam Trek") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Info trek gula hari ini
            Text(
                text = "Trek Gula-mu Saat Ini",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Jika kamu menyimpan produk ini, konsumsi gulamu hari ini akan menjadi:",
                style = MaterialTheme.typography.bodySmall
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Sebelum: ${"%.1f".format(state.totalBefore)} g")
                    Text("Produk ini: ${"%.1f".format(state.sugarGram)} g")
                    Text("Setelah: ${"%.1f".format(state.totalAfter)} g")
                }
            }

            Divider()

            Text(
                text = "Informasi Produk",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Masukkan nama produk yang baru saja kamu scan:",
                style = MaterialTheme.typography.bodySmall
            )

            OutlinedTextField(
                value = state.productName,
                onValueChange = { viewModel.onProductNameChange(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Contoh: Sereal Coklat 30g") }
            )

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onBack
                ) {
                    Text("Kembali")
                }

                Button(
                    modifier = Modifier.weight(1f),
                    enabled = !state.isLoading && state.productName.isNotBlank(),
                    onClick = {
                        viewModel.save(
                            onSuccess = onSaved,
                            onError = { /* bisa tampilkan snackbar kalau mau */ }
                        )
                    }
                ) {
                    Text(if (state.isLoading) "Menyimpan..." else "Simpan")
                }
            }
        }
    }
}
