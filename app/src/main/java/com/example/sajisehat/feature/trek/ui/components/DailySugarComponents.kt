package com.example.sajisehat.feature.trek.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.sajisehat.R
import com.example.sajisehat.feature.trek.model.SugarLevelUi
import com.example.sajisehat.feature.trek.model.calculateDailyPercent
import com.example.sajisehat.feature.trek.model.getDailySugarLevel
import com.example.sajisehat.feature.trek.model.titleText

@Composable
fun DailySugarSummaryCard(
    totalSugarGram: Double,
    modifier: Modifier = Modifier
) {
    val level = getDailySugarLevel(totalSugarGram)
    val limitGram = 50

    val cardStrokeColor = Color(0xFFDDDDDD)
    val cardGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFDFDFE),
            Color(0xFFF3F4F7)
        )
    )

    val sugarIconRes = when (level) {
        SugarLevelUi.LOW    -> R.drawable.ic_sugar_low
        SugarLevelUi.MEDIUM -> R.drawable.ic_sugar_medium
        SugarLevelUi.HIGH   -> R.drawable.ic_sugar_high
        SugarLevelUi.UNKNOWN -> R.drawable.ic_sugar_medium
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 2.dp,
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, cardStrokeColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(cardGradient)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = sugarIconRes),
                contentDescription = null,
                modifier = Modifier.size(72.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Konsumsi Gula: ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002A7A)
                            )
                        ) {
                            append(level.titleText())
                        }
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF222222)
                    )
                )

                Text(
                    text = buildAnnotatedString {
                        append("Hari ini, kamu telah mengonsumsi ")
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF002A7A)
                            )
                        ) {
                            append("${"%.0f".format(totalSugarGram)} gram")
                        }
                        append(" gula")
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF222222)
                    )
                )

                Text(
                    text = "Tahukah kamu? WHO menyarankan bahwa standar gula harian manusia adalah tidak lebih dari $limitGram gram",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF9E9E9E)
                    )
                )
            }
        }
    }
}

