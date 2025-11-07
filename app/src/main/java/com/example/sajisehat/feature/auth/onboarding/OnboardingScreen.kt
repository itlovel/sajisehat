package com.example.sajisehat.feature.auth.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import kotlinx.coroutines.launch

data class OnboardingScreen(
    val imageRes: Int,
    val title: String,
    val body: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    vm: OnboardingViewModel = viewModel()
) {
    val pages = remember {
        listOf(
            OnboardingPage(
                R.drawable.onboard_1,
                "Kenali Gula Tersembunyi!",
                "Scan label kemasan dan ketahui kadar gula dengan cepat."
            ),
            OnboardingPage(
                R.drawable.onboard_2,
                "Pantau Konsumsi Harian!",
                "Cek total asupan gula harianmu dan pastikan tetap dalam batas aman."
            ),
            OnboardingPage(
                R.drawable.onboard_3,
                "Hidup Sehat, Lebih Seru!",
                "Temukan tips sehat dan jadikan hidupmu lebih seimbang."
            )
        )
    }

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {

        // Background image per halaman
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(pages[page].imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        // PANEL NAVY — full width, rounded di atas saja
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(), // jaga dari 3-button/gesture bar
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp), // bawah = 0 (default)
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 24.dp), // padding internal konten
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = pages[pagerState.currentPage].title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = pages[pagerState.currentPage].body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.height(20.dp))

                    // ── Page indicator: pill kuning (aktif) + dot putih (non-aktif)
                    PageIndicator(
                        count = pages.size,
                        current = pagerState.currentPage,
                        activeColor = MaterialTheme.colorScheme.secondary,          // kuning
                        inactiveColor = MaterialTheme.colorScheme.onPrimary.copy(.6f)
                    )
                }

                // Tombol panah kuning di kanan bawah panel
                FilledIconButton(
                    onClick = {
                        if (pagerState.currentPage < pages.lastIndex) {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            vm.setDone(true)
                            onFinish()
                        }
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    )
                ) {
                    Icon(Icons.Outlined.ArrowForward, contentDescription = "Next")
                }
            }
        }
    }
}

@Composable
private fun PageIndicator(
    count: Int,
    current: Int,
    activeColor: androidx.compose.ui.graphics.Color,
    inactiveColor: androidx.compose.ui.graphics.Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(count) { i ->
            if (i == current) {
                // pill kuning (seperti referensi)
                Box(
                    Modifier
                        .width(28.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(activeColor)
                )
            } else {
                // dot kecil
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(inactiveColor)
                )
            }
            if (i != count - 1) Spacer(Modifier.width(10.dp))
        }
    }
}
