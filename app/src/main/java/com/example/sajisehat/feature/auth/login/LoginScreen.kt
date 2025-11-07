package com.example.sajisehat.feature.auth.login

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
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
import com.example.sajisehat.ui.util.findActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    onEmailSignIn: () -> Unit = onRegister,   // arahkan ke layar login email kalau ada
    vm: LoginViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    // Navigate ke Home ketika sudah login
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    // ------ Fallback Google Sign-In (Play Services) ------
    val ctx = LocalContext.current
    val activity = remember(ctx) { ctx.findActivity() }

    val classicLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            vm.signInWithFirebaseIdToken(account.idToken)
        } catch (e: Exception) {
            // kirim error sederhana; ViewModel-mu bisa menyediakan helper jika mau
            // di sini cukup tampilkan dialog default yang sudah ada
            vm.setError(e.message ?: "Tidak bisa masuk dengan Google")
        }
    }

    fun doGoogleAuth() {
        vm.signInWithGoogle(
            activity = activity,
            onFallback = { serverClientId, _ /*unused*/ ->
                // Siapkan Google Sign-In klasik & luncurkan chooser
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(serverClientId)
                    .requestEmail()
                    .build()
                val client = GoogleSignIn.getClient(activity, gso)
                classicLauncher.launch(client.signInIntent)
            }
        )
    }

    // ----------------- UI -----------------
    Surface(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(28.dp))

                Image(
                    painter = painterResource(R.drawable.ic_app_logo_large),
                    contentDescription = "Logo SAJISEHAT",
                    modifier = Modifier.size(120.dp)
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

                // ---------- Masuk dengan Google ----------
                OutlinedButton(
                    onClick = { doGoogleAuth() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Masuk dengan Google")
                }

                Spacer(Modifier.height(16.dp))

                // ---------- Masuk dengan Email ----------
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

                // ---------- Separator ----------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text("  jika belum memiliki akun  ", style = MaterialTheme.typography.labelLarge)
                    Divider(Modifier.weight(1f))
                }

                Spacer(Modifier.height(20.dp))

                // ---------- CTA Register flow (manual email) ----------
                Button(
                    onClick = onRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large
                ) { Text("Setuju & Daftar") }

                Spacer(Modifier.height(16.dp))

                // ---------- Daftar dengan Google (opsi tambahan) ----------
                OutlinedButton(
                    onClick = { doGoogleAuth() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Daftar dengan Google")
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Dengan mengetuk Setuju & Daftar, Anda menyetujui Ketentuan Layanan, Kebijakan Privasi, dan Kebijakan Cookie SAJISEHAT.",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }

    // ---------- Error dialog ----------
    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = vm::clearError,
            confirmButton = { TextButton(onClick = vm::clearError) { Text("OK") } },
            title = { Text("Gagal Masuk") },
            text = { Text(msg) }
        )
    }

}
