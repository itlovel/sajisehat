package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import com.example.sajisehat.ui.components.CompactOutlinedTextField

@Composable
fun RegisterPasswordScreen(
    onSuccess: () -> Unit,
    vm: RegisterViewModel
) {
    val st by vm.state.collectAsState()
    var show by remember { mutableStateOf(false) }

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            RegisterProgress(title = "buat akun", percent = st.stepPercent)

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Atur Kata Sandi Anda",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(Modifier.height(16.dp))


            RequiredLabel("Email")
            CompactOutlinedTextField(
                value = st.email,
                onValueChange = {},
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                placeholder = "nama@domain.com",
                shape = MaterialTheme.shapes.large
            )

            Spacer(Modifier.height(12.dp))


            RequiredLabel("Password")
            CompactOutlinedTextField(
                value = st.password,
                onValueChange = vm::updatePassword,
                visualTransformation = if (show)
                    androidx.compose.ui.text.input.VisualTransformation.None
                else
                    androidx.compose.ui.text.input.PasswordVisualTransformation(),
                trailing = {
                    IconButton(onClick = { show = !show }) {
                        Icon(
                            imageVector = if (show) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                shape = MaterialTheme.shapes.large
            )


            Spacer(Modifier.height(6.dp))
            Text(
                text = "Password must be 6+ characters",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(12.dp))

            // Remember me + Learn more
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = st.rememberMe,
                    onCheckedChange = { vm.toggleRemember() }
                )
                Spacer(Modifier.width(6.dp))
                val tag = "learn"
                val txt = buildAnnotatedString {
                    append("Remember me. ")
                    pushStringAnnotation(tag, "learn_more")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSecondary)) {
                        append("Learn more")
                    }
                    pop()
                }
                ClickableText(
                    text = txt,
                    style = MaterialTheme.typography.bodySmall,
                    onClick = { /* TODO: buka halaman bantuan jika ada */ }
                )
            }

            // Error (jika ada)
            st.error?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { vm.submit(onSuccess) },
                enabled = !st.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (st.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Lanjut", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

/** Label wajib (*) di atas field – konsisten dengan screen lain. */
@Composable
private fun RequiredLabel(text: String) {
    val label = buildAnnotatedString {
        append(text)
        append(" ")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) { append("*") }
    }
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 6.dp)
    )
}
