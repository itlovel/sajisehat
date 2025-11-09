package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.sajisehat.ui.components.CompactOutlinedTextField
import com.example.sajisehat.ui.theme.SajiTextStyles

@Composable
fun RegisterNameScreen(
    onNext: () -> Unit,
    vm: RegisterViewModel
) {
    val st by vm.state.collectAsState()

    val h = LocalConfiguration.current.screenHeightDp
    val topGap = (h * 0.035f).dp

    Surface(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(topGap))
            RegisterProgress(title = "buat akun", percent = st.stepPercent)

            Spacer(Modifier.height(50.dp))
            Text(
                text = "Masukkan Nama Anda",
                style = SajiTextStyles.H4Bold
            )

            Spacer(Modifier.height(16.dp))

            RequiredLabel("Nama Depan")
            CompactOutlinedTextField(
                value = st.firstName,
                onValueChange = vm::updateFirst,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                shape = MaterialTheme.shapes.large
            )

            Spacer(Modifier.height(12.dp))


            RequiredLabel("Nama Belakang")
            CompactOutlinedTextField(
                value = st.lastName,
                onValueChange = vm::updateLast,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                shape = MaterialTheme.shapes.large
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { vm.moveToEmail(); onNext() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Lanjut", style = SajiTextStyles.BodyLargeBold)
            }
        }
    }
}

@Composable
private fun RequiredLabel(text: String) {
    val styled = androidx.compose.ui.text.buildAnnotatedString {
        append(text)
        append(" ")
        withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) { append("*") }
    }
    Text(
        text = styled,
        style = SajiTextStyles.Body,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 6.dp)
    )
}
