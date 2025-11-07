package com.example.sajisehat.feature.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email

/**
 * Login sesuai desain:
 * - Logo aplikasi
 * - Judul + subjudul
 * - 2 tombol Outlined ("Masuk dengan Google" & "Masuk dengan Email") + ikon
 * - Separator "jika belum memiliki akun"
 * - Tombol primary "Setuju & Daftar"
 * - Teks persetujuan di bagian bawah
 *
 * onRegister -> ke flow register (Name->Email->Password)
 * onLoggedIn -> ke Home setelah sukses Firebase
 * onEmailSignIn -> opsional, kalau kamu punya layar login-email khusus
 */
@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    onEmailSignIn: () -> Unit = onRegister, // default: arahkan ke register kalau belum ada layar login-email
    vm: LoginViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val state by vm.state.collectAsState()

    // Auto navigate jika sudah login
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    // UI
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(28.dp))

                // LOGO — ganti ke logo kamu. Pakai launcher_foreground agar pasti tampil.
                Image(
                    painter = painterResource(R.drawable.ic_app_logo_large),
                    contentDescription = "Logo SAJISEHAT",
                    modifier = Modifier
                        .size(120.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "SAJISEHAT",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Cek gula, pantau konsumsi gula, dan temukan tips sehat untuk hidup lebih seimbang",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(28.dp))

                // ---- Tombol Masuk dengan Google ----
                OutlinedButton(
                    onClick = { vm.signInWithGoogle(ctx) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_google), // vector di bawah
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Masuk dengan Google")
                }

                Spacer(Modifier.height(16.dp))

                // ---- Tombol Masuk dengan Email ----
                OutlinedButton(
                    onClick = onEmailSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(Icons.Outlined.Email, contentDescription = null)
                    Spacer(Modifier.width(12.dp))
                    Text("Masuk dengan Email")
                }

                Spacer(Modifier.height(28.dp))

                // ---- Separator "jika belum memiliki akun" ----
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(modifier = Modifier.weight(1f))
                    Text(
                        "  jika belum memiliki akun  ",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Divider(modifier = Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))

                // ---- CTA "Setuju & Daftar" ----
                Button(
                    onClick = onRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,   // navy
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Setuju & Daftar") }

                Spacer(Modifier.height(16.dp))

                // ---- Legal text bawah ----
                Text(
                    text = "Dengan mengetuk Setuju & Daftar, Anda menyetujui Ketentuan Layanan, Kebijakan Privasi, dan Kebijakan Cookie SAJISEHAT.",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }

    // Error dialog sederhana
    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = vm::clearError,
            confirmButton = {
                TextButton(onClick = vm::clearError) { Text("OK") }
            },
            title = { Text("Gagal Masuk") },
            text = { Text(msg) }
        )
    }
}
