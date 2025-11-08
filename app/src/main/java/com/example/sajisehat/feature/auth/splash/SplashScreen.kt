package com.example.sajisehat.feature.auth.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import com.example.sajisehat.ui.theme.SajiTextStyles

@Composable
fun SplashScreen(
    onNav: (SplashNav) -> Unit = {},
    vm: SplashViewModel = viewModel()
) {
    LaunchedEffect(Unit) { vm.decide(onNav) }

    Surface(Modifier.fillMaxSize()) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            // ukuran logo & seberapa jauh digeser turun (dari titik tengah)
            val logoSize = maxHeight * 0.19f        // ~19% tinggi layar
            val centerShift = maxHeight * 0.01f     // geser 1% ke bawah

            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = centerShift)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_app_logo_large),
                    contentDescription = null,
                    modifier = Modifier.size(logoSize),
                    contentScale = ContentScale.Fit
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "SAJISEHAT",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "Baca Labelnya, Jaga Gula-nya,\nSehat Raganya!",
                    style = SajiTextStyles.BodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
