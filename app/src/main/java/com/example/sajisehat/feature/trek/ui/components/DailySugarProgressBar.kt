package com.example.sajisehat.feature.trek.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.sajisehat.feature.trek.model.calculateDailyPercent

@Composable
fun DailySugarProgressBar(
    totalNow: Double,          // konsumsi gula yang sudah ada
    addedSugar: Double = 0.0,  // gula dari produk yang akan ditambahkan (default 0 → full biru)
    modifier: Modifier = Modifier
) {
    val limitGram = 50.0
    val totalAfter = totalNow + addedSugar

    val dailyPercent = calculateDailyPercent(totalAfter)
    val percentForBar = dailyPercent.coerceIn(0, 200)

    // fraksi (0..1) terhadap limit harian
    val beforeFraction = (totalNow / limitGram).coerceIn(0.0, 1.0)
    val afterFraction = (totalAfter / limitGram).coerceIn(0.0, 1.0)
    val addedFraction = (afterFraction - beforeFraction)
        .coerceIn(0.0, 1.0 - beforeFraction)

    val cardStrokeColor = Color(0xFFDDDDDD)
    val cardBg = Brush.verticalGradient(
        listOf(
            Color(0xFFFFFFFF),
            Color(0xFFF5F5F8)
        )
    )

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, cardStrokeColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardBg)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Teks di atas bar
            Text(
                text = buildAnnotatedString {
                    append("Konsumsi gula-mu saat ini setara dengan ")
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF002A7A)
                        )
                    ) {
                        append("$dailyPercent%")
                    }
                    append(" dari kebutuhan gula-mu di hari ini")
                },
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF444444)
                )
            )

            // Bar abu + biru + kuning
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(50))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(50.dp))
                ) {
                    // Biru: konsumsi yang sudah ada
                    if (beforeFraction > 0.0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(beforeFraction.toFloat())
                                .background(Color(0xFF002A7A))
                        )
                    }

                    // Kuning: tambahan produk baru
                    if (addedFraction > 0.0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(addedFraction.toFloat())
                                .background(Color(0xFFFFC107))
                        )
                    }

                    // Sisa bar (abu)
                    val remaining = 1f - (beforeFraction + addedFraction).toFloat()
                    if (remaining > 0f) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(remaining)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "0 Gr",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF777777)
                    )
                )
                Text(
                    "${limitGram.toInt()} Gr",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFF777777)
                    )
                )
            }
        }
    }
}

