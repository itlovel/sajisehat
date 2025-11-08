package com.example.sajisehat.feature.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import com.example.sajisehat.ui.theme.SajiTextStyles

@Composable
fun LoginEmailScreen(
    onLoggedIn: () -> Unit,
    vm: EmailLoginViewModel = viewModel()
) {
    val ctx = LocalContext.current
    val st by vm.state.collectAsState()
    var showPassword by remember { mutableStateOf(false) }

    val h = LocalConfiguration.current.screenHeightDp
    val topGap = (h * 0.035f).dp
    val logoSize = (h * 0.12f).dp

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(topGap))

            // Logo
            Image(
                painter = painterResource(R.drawable.ic_app_logo_large),
                contentDescription = null,
                modifier = Modifier.size(logoSize)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "SAJISEHAT",
                style = SajiTextStyles.H5Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))
            Text(
                text = "Masuk dengan akun yang sudah ada",
                style = SajiTextStyles.Body,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(26.dp))


            RequiredLabel(text = "Email")
            com.example.sajisehat.ui.components.CompactOutlinedTextField(
                value = st.email,
                onValueChange = vm::updateEmail,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    imeAction   = androidx.compose.ui.text.input.ImeAction.Next
                )
            )

            Spacer(Modifier.height(14.dp))

            // ====== Label Password (di luar field) ======
            RequiredLabel(text = "Password")
            com.example.sajisehat.ui.components.CompactOutlinedTextField(
                value = st.password,
                onValueChange = vm::updatePassword,
                visualTransformation = if (showPassword) androidx.compose.ui.text.input.VisualTransformation.None
                else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                trailing = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword)
                                androidx.compose.material.icons.Icons.Outlined.Visibility
                            else
                                androidx.compose.material.icons.Icons.Outlined.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Password,
                    imeAction   = androidx.compose.ui.text.input.ImeAction.Done
                ),
                keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                    onDone = { if (!st.loading) vm.login(ctx, onLoggedIn) }
                )
            )

            Spacer(Modifier.height(10.dp))

            // Remember me + Learn more
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = st.remember, onCheckedChange = { vm.toggleRemember() })
                Spacer(Modifier.width(6.dp))
                val tag = "learn"
                val txt = buildAnnotatedString {
                    append("Remember me. ")
                    pushStringAnnotation(tag, "learn_more")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append("Learn more")
                    }
                    pop()
                }
                ClickableText(text = txt, style = SajiTextStyles.Caption, onClick = { _ -> })
            }

            Spacer(Modifier.height(20.dp))

            // Tombol Masuk (navy)
            Button(
                onClick = { vm.login(ctx, onLoggedIn) },
                enabled = !st.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
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
                    Text("Masuk", style = SajiTextStyles.BodyLargeBold)
                }
            }

            // Error (jika ada)
            st.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    it,
                    color = MaterialTheme.colorScheme.error,
                    style = SajiTextStyles.Caption,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


@Composable
private fun RequiredLabel(text: String) {
    val starColor = MaterialTheme.colorScheme.background
    val label = buildAnnotatedString {
        append(text)
        append(" ")
        withStyle(SpanStyle(color = starColor)) { append("*") }
    }
    Text(
        text = label,
        style = SajiTextStyles.Body,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 6.dp)
    )
}
