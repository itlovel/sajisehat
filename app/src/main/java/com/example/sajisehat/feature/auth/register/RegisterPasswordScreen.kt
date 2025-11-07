package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterPasswordScreen(
    onSuccess: () -> Unit,
    vm: RegisterViewModel = viewModel()
) {
    val st by vm.state.collectAsState()
    var show by remember { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(24.dp)
                .statusBarsPadding()
        ) {

            Spacer(Modifier.height(12.dp))
            RegisterProgress(title = "buat akun", percent = st.stepPercent)

            Spacer(Modifier.height(24.dp))
            Text(
                "Atur Kata Sandi Anda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = st.email, onValueChange = {}, enabled = false,
                label = { Text("Email*") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = st.password, onValueChange = vm::updatePassword,
                label = { Text("Password*") }, singleLine = true,
                visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { show = !show }) {
                        Icon(
                            imageVector = if (show) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (show) "Hide" else "Show"
                        )
                    }
                },
                supportingText = { Text("Password must be 6+ characters") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = st.rememberMe, onCheckedChange = { vm.toggleRemember() })
                Spacer(Modifier.width(6.dp))
                Text("Remember me. Learn more", style = MaterialTheme.typography.bodySmall)
            }

            if (st.error != null) {
                Spacer(Modifier.height(8.dp))
                Text(st.error!!, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { vm.submit(onSuccess) },
                enabled = !st.loading,
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            ) {
                if (st.loading) {
                    CircularProgressIndicator(
                        Modifier.size(18.dp), strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Lanjut")
                }
            }
        }
    }
}
