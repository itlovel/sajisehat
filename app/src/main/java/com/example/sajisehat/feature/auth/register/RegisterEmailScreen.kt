package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RegisterEmailScreen(
    onNext: () -> Unit,
    vm: RegisterViewModel
) {
    val st by vm.state.collectAsState()

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            RegisterProgress(title = "buat akun", percent = st.stepPercent)

            Spacer(Modifier.height(24.dp))
            Text(
                "Masukkan Email Anda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = st.email, onValueChange = vm::updateEmail,
                label = { Text("Email*") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.moveToPassword(); onNext() },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            ) { Text("Lanjut") }
        }
    }
}
