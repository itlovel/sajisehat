package com.example.sajisehat.feature.auth.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.R
import com.example.sajisehat.ui.theme.SajiTextStyles
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit,
    vm: OnboardingViewModel = viewModel()
) {
    val pages = vm.pages
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )
    val scope = rememberCoroutineScope()

    val cfg = LocalConfiguration.current
    val cardHeight = (cfg.screenHeightDp * 0.36f).dp.coerceIn(300.dp, 360.dp)

    Box(Modifier.fillMaxSize()) {

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            Image(
                painter = painterResource(pages[page].imageRes),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth,
                alignment = Alignment.TopCenter
            )
        }

        // CARD
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(cardHeight)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            color = MaterialTheme.colorScheme.primary
        ) {
            val current = pagerState.currentPage
            val p = pages[current]

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 70.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = p.title,
                    style = SajiTextStyles.H4Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.96f)
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = p.desc,
                    style = SajiTextStyles.Body,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                Spacer(Modifier.weight(1f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PageDots(
                        total = pages.size,
                        current = current,
                        modifier = Modifier.padding(start = 20.dp),
                        activeColor = MaterialTheme.colorScheme.secondary,
                        inactiveColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                    )

                    Spacer(Modifier.weight(1f))

                    IconButton(
                        onClick = {
                            if (current < pages.lastIndex) {
                                scope.launch { pagerState.animateScrollToPage(current + 1) }
                            } else {
                                vm.completeOnboarding()
                                onFinish()
                            }
                        },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_arrow_right),
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PageDots(
    total: Int,
    current: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.secondary,
    inactiveColor: Color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { i ->
            val isActive = i == current
            val width = animateDpAsState(targetValue = if (isActive) 20.dp else 6.dp, label = "dotW").value
            val color = animateColorAsState(targetValue = if (isActive) activeColor else inactiveColor, label = "dotC").value

            Box(
                Modifier
                    .height(6.dp)
                    .width(width)
                    .clip(RoundedCornerShape(3.dp))
                    .background(color)
            )

            if (i != total - 1) {
                Spacer(Modifier.width(10.dp))
            }
        }
    }
}
