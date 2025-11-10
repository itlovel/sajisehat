package com.example.sajisehat.feature.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScanCameraSection(
    isProcessing: Boolean,
    onPickFromGallery: () -> Unit,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // TOP BAR biru
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(Color(0xFF001A66)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Scan Label",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        // AREA “kamera”
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                val frameThickness = 4.dp
                val frameLength = 40.dp

                // corner frames
                // TOP-LEFT
                Box(
                    modifier = Modifier
                        .size(width = frameLength, height = frameThickness)
                        .align(Alignment.TopStart)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .size(width = frameThickness, height = frameLength)
                        .align(Alignment.TopStart)
                        .background(Color.White)
                )
                // TOP-RIGHT
                Box(
                    modifier = Modifier
                        .size(width = frameLength, height = frameThickness)
                        .align(Alignment.TopEnd)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .size(width = frameThickness, height = frameLength)
                        .align(Alignment.TopEnd)
                        .background(Color.White)
                )
                // BOTTOM-LEFT
                Box(
                    modifier = Modifier
                        .size(width = frameLength, height = frameThickness)
                        .align(Alignment.BottomStart)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .size(width = frameThickness, height = frameLength)
                        .align(Alignment.BottomStart)
                        .background(Color.White)
                )
                // BOTTOM-RIGHT
                Box(
                    modifier = Modifier
                        .size(width = frameLength, height = frameThickness)
                        .align(Alignment.BottomEnd)
                        .background(Color.White)
                )
                Box(
                    modifier = Modifier
                        .size(width = frameThickness, height = frameLength)
                        .align(Alignment.BottomEnd)
                        .background(Color.White)
                )

                Text(
                    text = if (isProcessing)
                        "Memproses hasil scan..."
                    else
                        "Arahkan label gizi ke dalam kotak",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 24.dp),
                    textAlign = TextAlign.Center
                )

                // tombol kuning: galeri & flash
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Gallery
                    Surface(
                        modifier = Modifier.size(44.dp),
                        color = Color(0xFFFFC72C),
                        shape = MaterialTheme.shapes.small,
                        shadowElevation = 4.dp,
                        onClick = onPickFromGallery
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🖼", fontSize = 20.sp)
                        }
                    }

                    // Flash (sementara belum fungsi, jadi kosong dulu)
                    Surface(
                        modifier = Modifier.size(44.dp),
                        color = Color(0xFFFFC72C),
                        shape = MaterialTheme.shapes.small,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("⚡", fontSize = 20.sp)
                        }
                    }
                }
            }
        }

        // tidak ada tombol bawah lagi
    }
}
