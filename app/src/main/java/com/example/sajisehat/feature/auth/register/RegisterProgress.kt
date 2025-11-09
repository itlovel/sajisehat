package com.example.sajisehat.feature.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.example.sajisehat.R
import com.example.sajisehat.SajisehatApp
import com.example.sajisehat.ui.theme.SajiTextStyles
import kotlin.math.min

@Composable
fun RegisterProgress(
    title: String,
    percent: Int,
    showLogo: Boolean = true,
) {
    val screenW = LocalConfiguration.current.screenWidthDp.dp
    val groupWidth = min(screenW * 0.65f, 320.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
    ) {
        // Logo
        if (showLogo) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_app_logo_large),
                    contentDescription = null,
                    modifier = Modifier.size(105.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(modifier = Modifier.width(groupWidth)) {
                if (title.isNotBlank()) {
                    Text(
                        text = title,
                        style = SajiTextStyles.BodyBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(8.dp))
                }

                LinearProgressIndicator(
                    progress = percent / 100f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "$percent%",
                        style = SajiTextStyles.BodySemibold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
