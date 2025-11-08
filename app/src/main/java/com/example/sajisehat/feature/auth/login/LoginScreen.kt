package com.example.sajisehat.feature.auth.login

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import com.example.sajisehat.ui.theme.SajiTextStyles
import com.example.sajisehat.ui.util.findActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlin.math.roundToInt

@Composable
fun LoginScreen(
    onRegister: () -> Unit,
    onLoggedIn: () -> Unit,
    onEmailSignIn: () -> Unit = onRegister,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.state.collectAsState()

    // Auto navigate bila sudah login
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoggedIn()
    }

    // ---------- Fallback Google Sign-In (Play Services) ----------
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
            vm.setError(e.message ?: "Tidak bisa masuk dengan Google")
        }
    }

    fun launchGoogle() {
        vm.signInWithGoogle(
            activity = activity,
            onFallback = { serverClientId, _ ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(serverClientId)
                    .requestEmail()
                    .build()
                val client = GoogleSignIn.getClient(activity, gso)
                classicLauncher.launch(client.signInIntent)
            }
        )
    }

    // ---------- UI ----------
    Surface(Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Skala responsif berdasarkan lebar (referensi maket ~ 360–440)
            val base = 390f
            val scale = (maxWidth.value / base).coerceIn(0.85f, 1.15f)
            fun s(v: Int): Dp = (v * scale).roundToInt().dp

            val corner = s(16)
            val outline = ButtonDefaults.outlinedButtonBorder
            val buttonShape = RoundedCornerShape(corner)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = s(20)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(s(28)))

                Image(
                    painter = painterResource(R.drawable.ic_app_logo_large),
                    contentDescription = "Logo SAJISEHAT",
                    modifier = Modifier.size(s(110))
                )

                Spacer(Modifier.height(s(16)))

                Text(
                    text = "SAJISEHAT",
                    style = SajiTextStyles.H5Bold,
//                        .copy(
//                        fontWeight = FontWeight.ExtraBold,
//                        fontSize = (22 * scale).sp
//                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Cek gula, pantau konsumsi gula, dan temukan tips sehat untuk hidup lebih seimbang",
                    style = SajiTextStyles.Body,
//                        .copy(fontSize = (14 * scale).sp)
                    textAlign = TextAlign.Center,
//                    lineHeight = (20 * scale).sp,
                    modifier = Modifier.padding(horizontal = s(4))
                )

                Spacer(Modifier.height(s(35)))

                // Masuk dengan Google
                OutlinedButton(
                    onClick = { launchGoogle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(s(45)),
                    shape = buttonShape,
                    border = outline,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    contentPadding = PaddingValues(horizontal = s(10))
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(s(20)),
                        // penting: jangan tint agar warna asli logo terlihat
                        colorFilter = null as ColorFilter?
                    )
                    Spacer(Modifier.width(s(12)))
                    Text("Masuk dengan Google", fontSize = (15 * scale).sp)
                }

                Spacer(Modifier.height(s(20)))

                // Masuk dengan Email
                OutlinedButton(
                    onClick = onEmailSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(s(45)),
                    shape = buttonShape,
                    border = outline,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    contentPadding = PaddingValues(horizontal = s(10))
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null,
                        modifier = Modifier.size(s(20))
                    )
                    Spacer(Modifier.width(s(12)))
                    Text("Masuk dengan Email", fontSize = (15 * scale).sp)
                }

                Spacer(Modifier.height(s(40)))
                // Separator “jika belum memiliki akun”
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Divider(Modifier.weight(1f))
                    Text(
                        "  jika belum memiliki akun  ",
                        style = MaterialTheme.typography.labelLarge.copy(fontSize = (12 * scale).sp)
                    )
                    Divider(Modifier.weight(1f))
                }

                Spacer(Modifier.height(s(40)))

                // Setuju & Daftar (primary navy)
                Button(
                    onClick = onRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(s(48)),
                    shape = buttonShape
                ) {
                    Text(
                        "Setuju & Daftar",
                        style = SajiTextStyles.BodyLargeBold,
//                            .copy(
//                            fontWeight = FontWeight.SemiBold,
//                            fontSize = (16 * scale).sp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
//                    )
                }

                Spacer(Modifier.height(s(20)))

                // Daftar dengan Google (outlined)
                OutlinedButton(
                    onClick = { launchGoogle() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(s(45)),
                    shape = buttonShape,
                    border = outline,
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    contentPadding = PaddingValues(horizontal = s(10))
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_google),
                        contentDescription = null,
                        modifier = Modifier.size(s(20)),
                        colorFilter = null as ColorFilter?
                    )
                    Spacer(Modifier.width(s(12)))
                    Text("Daftar dengan Google", fontSize = (15 * scale).sp)
                }

                Spacer(Modifier.height(s(14)))

                // Terms section, align center dan tampilan link
                Text(
                    text = termsText(
                        highlight = MaterialTheme.colorScheme.primary
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = (10 * scale).sp,
                        lineHeight = (14 * scale).sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(s(8)))
            }

            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }
        }
    }

    // Dialog error
    state.error?.let { msg ->
        AlertDialog(
            onDismissRequest = vm::clearError,
            confirmButton = { TextButton(onClick = vm::clearError) { Text("OK") } },
            title = { Text("Gagal Masuk") },
            text = { Text(msg) }
        )
    }
}

private fun termsText(highlight: androidx.compose.ui.graphics.Color): AnnotatedString =
    buildAnnotatedString {
        append("Dengan mengetuk Setuju & Daftar, Anda menyetujui ")
        withStyle(SpanStyle(color = highlight, fontWeight = FontWeight.SemiBold)) {
            append("Ketentuan Layanan")
        }
        append(", ")
        withStyle(SpanStyle(color = highlight, fontWeight = FontWeight.SemiBold)) {
            append("Kebijakan Privasi")
        }
        append(", dan ")
        withStyle(SpanStyle(color = highlight, fontWeight = FontWeight.SemiBold)) {
            append("Kebijakan Cookie ")
        }
        append("SAJISEHAT.")
    }
