package com.example.sajisehat.feature.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R

@Composable
fun LoginEmailScreen(
    onLoggedIn: () -> Unit,
    vm: EmailLoginViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val st by vm.state.collectAsState()
    var show by remember { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(28.dp))

            Image(
                painter = painterResource(R.drawable.ic_app_logo_large),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("SAJISEHAT", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold))
            Spacer(Modifier.height(8.dp))
            Text("Masuk dengan akun yang sudah ada", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = st.email, onValueChange = vm::updateEmail,
                label = { Text("Email*") }, singleLine = true,
                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = st.password, onValueChange = vm::updatePassword,
                label = { Text("Password*") }, singleLine = true,
                visualTransformation = if (show) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { show = !show }) {
                        Icon(if (show) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility, contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.large
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = st.remember, onCheckedChange = { vm.toggleRemember() })
                Spacer(Modifier.width(6.dp))
                Text("Remember me.  Learn more", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { vm.login(ctx, onLoggedIn) },
                enabled = !st.loading,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = MaterialTheme.shapes.large
            ) {
                if (st.loading) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                else Text("Masuk")
            }

            st.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
