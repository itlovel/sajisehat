package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sajisehat.R
import com.example.sajisehat.ui.theme.SajiTextStyles
import kotlinx.coroutines.delay

@Composable
fun RegisterSuccessScreen(onGoHome: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1200)
        onGoHome()
    }

    val h = LocalConfiguration.current.screenHeightDp
    val logoSize = (h * 0.20f).dp

    Surface(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.50f))

            Image(
                painter = painterResource(R.drawable.ic_app_logo_large),
                contentDescription = null,
                modifier = Modifier.size(logoSize)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "SAJISEHAT",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold)
            )

            Spacer(Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_badge_check),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(50.dp)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "buat akun",
                        style = SajiTextStyles.BodyBold
                    )
                    Text(
                        text = "sukses",
                        style = SajiTextStyles.H5Bold
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            RegisterProgress(
                title = "",
                percent = 100,
                showLogo = false
            )

            Spacer(Modifier.weight(0.88f))
        }
    }
}
