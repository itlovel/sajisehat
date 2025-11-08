package com.example.sajisehat.feature.scan

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.ImageDecoder
import android.os.Build
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView
import com.example.sajisehat.R

@Composable
fun SugarSummaryCard(
    level: SugarLevel,
    sugarPerServing: Double,
    dailyPercent: Int,
    modifier: Modifier = Modifier
) {
    val (levelText, accentColor, emojiRes) = when (level) {
        SugarLevel.RENDAH -> Triple(
            "Rendah",
            Color(0xFFF1C40F),
            R.drawable.gif_sugar_low
        )
        SugarLevel.SEDANG -> Triple(
            "Sedang",
            Color(0xFFF39C12),
            R.drawable.gif_sugar_medium
        )
        SugarLevel.TINGGI -> Triple(
            "Tinggi",
            Color(0xFFE74C3C),
            R.drawable.gif_sugar_high
        )
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF002A7A),
                            Color(0xFF21409A)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Emoji / GIF di kiri
                SugarGif(resId = emojiRes, modifier = Modifier.size(64.dp))

                // Teks di kanan
                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Kandungan Gula: $levelText",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "Produk mengandung %.1f Gram gula per 1 takaran saji-nya"
                            .format(sugarPerServing),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        text = "%d%% dari kebutuhan gulamu per hari"
                            .format(dailyPercent),
                        color = accentColor,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun SugarGif(
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        // decode sekali dan di-remember
        val drawable = remember(resId) {
            val source = ImageDecoder.createSource(context.resources, resId)
            ImageDecoder.decodeDrawable(source)   // biasanya AnimatedImageDrawable
        }

        AndroidView(
            modifier = modifier,
            factory = { ctx ->
                // di sini kita jelas pakai ImageView
                ImageView(ctx).apply {
                    setImageDrawable(drawable)
                    (drawable as? AnimatedImageDrawable)?.start()
                }
            },
            update = { imageView: ImageView ->
                imageView.setImageDrawable(drawable)
                (drawable as? AnimatedImageDrawable)?.start()
            }
        )
    } else {
        // API < 28 → tampilkan frame statis
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            modifier = modifier
        )
    }
}

