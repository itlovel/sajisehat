package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.sajisehat.feature.trek.model.*
import com.example.sajisehat.feature.trek.ui.components.DailySugarProgressBar
import com.example.sajisehat.feature.trek.ui.components.DailySugarSummaryCard
import java.time.format.TextStyle
import java.util.Locale

// ===================== TODAY SECTION =====================

@Composable
private fun TodaySection(
    today: TodaySummaryUi,
    onSeeDetailToday: () -> Unit
) {

    Spacer(Modifier.height(8.dp))

    Text(
        text = "Hari Ini",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(8.dp))

    // Card ringkasan (icon hati + teks)
    DailySugarSummaryCard(
        totalSugarGram = today.totalGram,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(12.dp))

    // Progress bar HARI INI → full biru (addedSugar = 0.0)
    DailySugarProgressBar(
        totalNow = today.totalGram,
        addedSugar = 0.0,              // full biru
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(Modifier.height(16.dp))

    // Tombol "Cek Detail" ala Figma: putih, border biru tua, teks agak kecil
    val darkBlue = Color(0xFF002A7A)

    OutlinedButton(
        onClick = onSeeDetailToday,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(24.dp),
        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp, brush = SolidColor(darkBlue)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = darkBlue
        )
    ) {
        Text(
            text = "Cek Detail >>",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}

// ===================== WEEK SECTION =====================
@Composable
private fun WeekSection(
    week: WeekSummaryUi
) {
    Text(
        text = "Minggu Ini",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        // background gradasi biru
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF001452), // biru tua atas
                            Color(0xFF002A7A)  // sedikit lebih cerah bawah
                        )
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(vertical = 16.dp)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // "Minggu ke-3 Mei" → bagian ke-3 Mei bold kuning
                Text(
                    text = buildAnnotatedString {
                        append("Minggu ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD966)
                            )
                        ) {
                            append(week.label.removePrefix("Minggu "))
                        }
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Notes: ${week.note}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                val listState = rememberLazyListState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp) // tinggi area lingkaran + text hari
                ) {
                    // Lingkaran hari – scroll horizontal
                    LazyRow(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        // pakai count-based overload
                        items(week.days.size) { index ->
                            val day = week.days[index]
                            WeekDayBubble(day)
                        }
                    }

                    // Fade di kanan + icon » sebagai hint swipe
                    // ==== di dalam Box minggu, ganti overlay kanan lama dengan ini ====
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(64.dp)          // sedikit lebih lebar supaya gradasinya halus
                    ) {
                        // layer fade yang lebih soft
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color.Transparent,
                                            Color(0xFF002A7A).copy(alpha = 0.35f),
                                            Color(0xFF002A7A).copy(alpha = 0.85f)
                                        )
                                    )
                                )
                        )

                        // icon » kecil, agak masuk ke dalam
                        Text(
                            text = "»",
                            style = MaterialTheme.typography.titleMedium,   // lebih kecil dari sebelumnya
                            color = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                        )
                    }

                }
            }
        }
    }
}


@Composable
private fun WeekDayBubble(day: WeekDayUi) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // lingkaran kuning gradasi
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFFFF59D), // kuning terang atas
                            Color(0xFFFBC02D)  // kuning lebih gelap bawah
                        )
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.totalGram.toInt().toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF001452)
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = day.date.dayOfWeek.getDisplayName(
                TextStyle.SHORT,
                Locale("id")
            ),
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

// ===================== MONTH SECTION =====================

// gunakan mapping yg sama dengan harian, supaya 17 gram tetap "Rendah"
private fun Double.toSugarLevel(): SugarLevelUi =
    getDailySugarLevel(this)

private fun SugarLevelUi.toColor(): Color = when (this) {
    SugarLevelUi.LOW -> Color(0xFFF5F5F5)      // abu muda
    SugarLevelUi.MEDIUM -> Color(0xFFFFF59D)   // kuning muda
    SugarLevelUi.HIGH -> Color(0xFFFFC107)     // kuning lebih kuat
    SugarLevelUi.UNKNOWN -> Color(0xFFE0E0E0)
}

@Composable
private fun MonthSection(
    month: MonthSummaryUi,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {

    Spacer(Modifier.height(8.dp))

    Text(
        text = "Bulan Ini",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(8.dp))

    val darkBlue = Color(0xFF001452)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(1.dp, darkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header bulan + panah
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPrevMonth) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Bulan sebelumnya"
                    )
                }

                val monthName = month.yearMonth.month.getDisplayName(
                    TextStyle.FULL,
                    Locale("id")
                )
                Text(
                    text = "$monthName ${month.yearMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onNextMonth) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Bulan berikutnya"
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Header hari
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN").forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            CalendarGrid(month)

            Spacer(Modifier.height(16.dp))

            LegendRow()
        }
    }
}

@Composable
private fun CalendarGrid(month: MonthSummaryUi) {
    val yearMonth = month.yearMonth
    val firstDay = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstIndex = (firstDay.dayOfWeek.value % 7) // Senin=1 → 1

    val map = month.days.associateBy { it.date }

    val cells = mutableListOf<MonthDayUi?>()

    // kosong sebelum tanggal 1
    repeat(firstIndex - 1) { cells.add(null) }

    for (day in 1..daysInMonth) {
        val date = yearMonth.atDay(day)
        val dayData = map[date] ?: MonthDayUi(date, 0.0)
        cells.add(dayData)
    }

    cells.chunked(7).forEach { weekRow ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            weekRow.forEach { day ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (day == null) {
                        // kosong
                    } else {
                        val level = day.totalGram.toSugarLevel()
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = level.toColor()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendRow() {
    Column {
        Text(
            text = "Petunjuk:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(4.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = SugarLevelUi.LOW.toColor(), label = "Rendah")
            LegendItem(color = SugarLevelUi.MEDIUM.toColor(), label = "Sedang")
            LegendItem(color = SugarLevelUi.HIGH.toColor(), label = "Tinggi")
        }
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

// ===================== ROOT SCREEN =====================

@Composable
fun TrekScreen(
    state: TrekUiState,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSeeDetailToday: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Trek Gula",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Siap jaga asupan gulamu hari ini?",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(16.dp))

        state.todaySummary?.let {
            TodaySection(
                today = it,
                onSeeDetailToday = onSeeDetailToday
            )
        }

        Spacer(Modifier.height(24.dp))

        state.weekSummary?.let {
            WeekSection(week = it)
        }

        Spacer(Modifier.height(24.dp))

        state.monthSummary?.let {
            MonthSection(
                month = it,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth
            )
        }

        Spacer(Modifier.height(32.dp))
    }
}
