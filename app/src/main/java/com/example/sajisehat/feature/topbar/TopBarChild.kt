package com.example.sajisehat.feature.topbar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sajisehat.R

/**
 * Top bar sederhana untuk halaman child (detail, hasil scan, dsb.)
 * - Default: background biru tua, teks putih
 * - Bisa pakai back button atau tidak
 */
@Composable
fun TopBarChild(
    title: String,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    showBack: Boolean = onBack != null,
    containerColor: Color = Color(0xFF00084F),
    contentColor: Color = Color.White,
    height: Dp = 56.dp,
    roundedBottomRadius: Dp = 16.dp
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(
            bottomStart = roundedBottomRadius,
            bottomEnd = roundedBottomRadius
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack && onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        painter = painterResource(R.drawable.ic_back),
                        contentDescription = "Kembali",
                        tint = contentColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = contentColor
            )
        }
    }
}