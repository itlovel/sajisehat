package com.example.sajisehat.feature.home.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.sajisehat.feature.topbar.TopBarEvent
import com.example.sajisehat.feature.topbar.TopBarViewModel
import com.example.sajisehat.ui.components.topbar.AppTopBar
import com.example.sajisehat.ui.theme.SajiTextStyles
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onOpen: (String) -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenNotification: () -> Unit = {},
    topBarViewModel: TopBarViewModel = viewModel(),
    vm: HomeViewModel = viewModel()
) {
    val st by vm.state.collectAsState()

    LaunchedEffect(st.userName, st.userPhotoUrl) {
        topBarViewModel.setUser(st.userName, st.userPhotoUrl)
    }

    Scaffold(
        topBar = {
            AppTopBar(
                state = topBarViewModel.state.collectAsState().value,
                onEvent = {
                    when (it) {
                        TopBarEvent.OnAvatarClick -> onOpenProfile()
                        TopBarEvent.OnBellClick   -> onOpenNotification()
                        else -> topBarViewModel.onEvent(it)
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(30.dp))

            TipCarousel(
                tips = st.tips,
                loading = st.loadingTips,
                error = st.errorTips
            )

            Spacer(Modifier.height(25.dp))

            CategoryCard(
                onAddManual  = { onOpen("addManual") },
                onReadArticle = { onOpen("articles") },
                onWatchVideo = { onOpen("videos") }
            )

            Spacer(Modifier.height(20.dp))

            SugarTrackCarousel(
                day = st.dayGrams,
                week = st.weekGrams,
                month = st.monthGrams,
                loading = st.loadingSugar
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TipCarousel(
    tips: List<TipUi>,
    loading: Boolean,
    error: String?
) {
    val width = LocalConfiguration.current.screenWidthDp.dp
    val cardHeight = (width * 0.40f).coerceAtLeast(140.dp)

    if (loading) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(cardHeight),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {}
        return
    }
    if (error != null || tips.isEmpty()) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(cardHeight),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Tip belum tersedia", style = SajiTextStyles.Body)
        } }
        return
    }

    val pager = rememberPagerState(initialPage = 0) { tips.size }
    val scope = rememberCoroutineScope()
    LaunchedEffect(pager.currentPage, tips.size) {
        if (tips.size > 1) {
            delay(4000)
            scope.launch { pager.animateScrollToPage((pager.currentPage + 1) % tips.size) }
        }
    }

    HorizontalPager(
        state = pager,
        modifier = Modifier.fillMaxWidth().height(cardHeight)
    ) { page ->
        TipCard(tips[page])
    }
}

@Composable
private fun TipCard(item: TipUi) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.50f)
    )
    {
        Row(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalAlignment = Alignment.CenterVertically

        )  {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxHeight().weight(0.9f)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1.6f)) {
                Text(
                    "\uD83D\uDC40 Tip Of The Day",
                    style = SajiTextStyles.BodyLargeBold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(Modifier.height(6.dp))
                Text(item.text, style = SajiTextStyles.Caption,
                    color = MaterialTheme.colorScheme.onSecondaryContainer)
            }
        }
    }
}


private enum class TrackTab(val title: String) { DAY("HARI INI"), WEEK("MINGGU INI"), MONTH("BULAN INI") }

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SugarTrackCarousel(
    day: Int,
    week: Int,
    month: Int,
    loading: Boolean
) {
    val pages = listOf(
        TrackTab.DAY to day.coerceAtLeast(0),
        TrackTab.WEEK to week.coerceAtLeast(0),
        TrackTab.MONTH to month.coerceAtLeast(0)
    )

    val pager = rememberPagerState(initialPage = 1) { pages.size } // default "MINGGU INI"
    val scope = rememberCoroutineScope()
    val width = LocalConfiguration.current.screenWidthDp.dp

    val widthDp = LocalConfiguration.current.screenWidthDp
    val arrowButtonSize = when {
        widthDp < 360 -> 66.dp   // HP kecil
        widthDp < 400 -> 70.dp   // HP sedang
        else          -> 74.dp   // HP lebar
    }
    val arrowIconSize = when {
        widthDp < 360 -> 60.dp
        widthDp < 400 -> 62.dp
        else          -> 64.dp
    }


    val cardHeight = (width * 0.62f).coerceIn(220.dp, 300.dp)

    if (loading) {
        Surface(
            modifier = Modifier.fillMaxWidth().height(cardHeight),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ) {}
        return
    }

    Box {
        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxWidth().height(cardHeight)
        ) { idx ->
            val (tab, grams) = pages[idx]
            SugarTrackCard(
                title = tab.title,
                grams = grams,
                note = noteFor(tab, grams)
            )
        }

        IconButton(
            onClick = { scope.launch { pager.animateScrollToPage((pager.currentPage - 1).coerceAtLeast(0)) } },
            enabled = pager.currentPage > 0,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                .clip(CircleShape).size(arrowButtonSize)
        ) { Icon(
            Icons.Rounded.ChevronLeft,
            contentDescription = "Sebelumnya",
            modifier = Modifier.size(arrowIconSize)
        )
        }

        IconButton(
            onClick = { scope.launch { pager.animateScrollToPage((pager.currentPage + 1).coerceAtMost(pages.lastIndex)) } },
            enabled = pager.currentPage < pages.lastIndex,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 8.dp)
                .clip(CircleShape).size(arrowButtonSize)
        ) { Icon(
            Icons.Rounded.ChevronRight,
            contentDescription = "Berikutnya",
            modifier = Modifier.size(arrowIconSize)
        )
        }
    }
}

@Composable
private fun SugarTrackCard(
    title: String,
    grams: Int,
    note: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.outline
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, style = SajiTextStyles.H5Bold,
                color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text("Konsumsi Gulamu:", style = SajiTextStyles.BodySemibold,
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            Text(grams.toString(), style = MaterialTheme.typography.displayLarge.copy(fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(6.dp))
            Text("Gram Gula", style = SajiTextStyles.BodyBold,
                color = MaterialTheme.colorScheme.secondary)
            Spacer(Modifier.height(8.dp))
            Text(note, style = SajiTextStyles.CaptionSemibold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                textAlign = TextAlign.Center)
        }
    }
}

private fun noteFor(tab: TrackTab, grams: Int): String = when (tab) {
    TrackTab.DAY   -> if (grams >= 50) "Catatan: Udah hampir maks nih, diperhatikan lagi yaa" else "Terus jaga konsumsi gulamu hari ini ya!"
    TrackTab.WEEK  -> if (grams >= 350) "Catatan: Waduh, hampir mencapai batas maksimum asupan gula nih!" else "Mantap! Minggu ini masih aman."
    TrackTab.MONTH -> if (grams >= 1500) "Catatan: Hati-hati, bulan ini mendekati batas rekomendasi." else "Stabil! Bulan ini masih dalam batas sehat."
}
