package com.example.sajisehat.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun LoadingDialog(
    message: String = "Memproses...",
    dismissible: Boolean = false,
    onDismissRequest: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = {
            if (dismissible) {
                onDismissRequest?.invoke()
            }
        }
    ) {
        val colorScheme = MaterialTheme.colorScheme

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = colorScheme.surface,
            tonalElevation = 8.dp,
            border = BorderStroke(
                width = 1.dp,
                color = colorScheme.primary.copy(alpha = 0.25f)
            )
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lingkaran lembut + spinner
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .alpha(0.98f),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape),
                        color = colorScheme.primary.copy(alpha = 0.08f),
                        tonalElevation = 0.dp
                    ) {}

                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
