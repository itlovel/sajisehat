package com.example.sajisehat.feature.trek.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.example.sajisehat.feature.trek.model.*
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.draw.clip

@Composable
private fun TodaySection(
    today: TodaySummaryUi,
    onSeeDetailToday: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Hari Ini",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Konsumsi Gula: ${today.level.toDisplayText()}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Hari ini, kamu telah mengonsumsi ${today.totalGram.toInt()} gram gula",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "WHO menyarankan bahwa standar gula harian manusia adalah tidak lebih dari 50 gram.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Konsumsi gula-mu saat ini setara dengan ${today.percentageOfNeed}% dari kebutuhan gula-mu di hari ini",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = (today.percentageOfNeed / 100f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(50)),
            )

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0 gr", style = MaterialTheme.typography.labelSmall)
                Text("50 gr", style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = onSeeDetailToday,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50)
            ) {
                Text("Cek Detail >>")
            }
        }
    }
}

private fun SugarLevelUi.toDisplayText(): String = when (this) {
    SugarLevelUi.LOW -> "Rendah"
    SugarLevelUi.MEDIUM -> "Sedang"
    SugarLevelUi.HIGH -> "Tinggi"
    SugarLevelUi.UNKNOWN -> "-"
}

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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF001452) // biru tua, bisa ganti ke warna theme
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = week.label,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Notes: ${week.note}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                week.days.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD966) // kuning bulat
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.totalGram.toInt().toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF001452)
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = day.date.dayOfWeek.getDisplayName(
                                TextStyle.SHORT,
                                Locale("id")
                            ), // "Sen", "Sel", dll
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

private fun Double.toSugarLevel(): SugarLevelUi = when {
    this <= 0.0 -> SugarLevelUi.UNKNOWN
    this <= 5.0 -> SugarLevelUi.LOW
    this <= 15.0 -> SugarLevelUi.MEDIUM
    else -> SugarLevelUi.HIGH
}

private fun SugarLevelUi.toColor(): Color = when (this) {
    SugarLevelUi.LOW -> Color(0xFFFFF2CC)    // rendah
    SugarLevelUi.MEDIUM -> Color(0xFFFFD966) // sedang
    SugarLevelUi.HIGH -> Color(0xFFF4B400)   // tinggi
    SugarLevelUi.UNKNOWN -> Color(0xFFE0E0E0)
}

@Composable
private fun MonthSection(
    month: MonthSummaryUi,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Text(
        text = "Bulan Ini",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Spacer(Modifier.height(8.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

