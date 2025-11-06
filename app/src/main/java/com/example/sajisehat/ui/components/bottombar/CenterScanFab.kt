package com.example.sajisehat.ui.components.bottombar

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CenterScanFab(
    selected: Boolean,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp,
    iconSize: Dp
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.then(Modifier.size(size)),
        shape = CircleShape, // ← pastikan lingkaran
        containerColor = if (selected)
            MaterialTheme.colorScheme.secondary
        else
            MaterialTheme.colorScheme.background, // krem/putih seperti referensi
        contentColor = if (selected)
            MaterialTheme.colorScheme.onSecondary
        else
            MaterialTheme.colorScheme.onBackground,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = "Scan",
            tint = Color.Unspecified,
            modifier = Modifier.size(iconSize)
        )
    }
}
