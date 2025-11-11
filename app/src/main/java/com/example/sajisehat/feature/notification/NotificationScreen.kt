package com.example.sajisehat.feature.notification

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.feature.topbar.TopBarChild
import com.example.sajisehat.ui.theme.SajiTextStyles
import java.time.LocalDate

@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    vm: NotificationViewModel = viewModel()
) {
    val st by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopBarChild(
                title = "Notifikasi",
                onBack = onBack
            )
        }
    ) { inner ->
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            when {
                st.loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                st.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = st.error ?: "Gagal memuat notifikasi",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> NotificationContent(items = st.items)
            }
        }
    }
}

@Composable
private fun NotificationContent(items: List<DailyNotificationUi>) {
    val today = LocalDate.now()
    val yesterday = today.minusDays(1)

    val todayItems = items.filter { it.date == today }
    val yesterdayItems = items.filter { it.date == yesterday }
    val weekItems = items.filter { it.date < yesterday }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(Modifier.height(16.dp)) }

        item {
            NotificationSectionTitle("Hari Ini")
        }
        items(todayItems) { item ->
            NotificationCard(item)
        }

        item { Spacer(Modifier.height(20.dp)) }

        if (yesterdayItems.isNotEmpty()) {
            item { NotificationSectionTitle("Kemarin") }
            items(yesterdayItems) { item ->
                NotificationCard(item)
            }
            item { Spacer(Modifier.height(20.dp)) }
        }

        if (weekItems.isNotEmpty()) {
            item { NotificationSectionTitle("Minggu Ini") }
            items(weekItems) { item ->
                NotificationCard(item)
            }
            item { Spacer(Modifier.height(20.dp)) }
        }

        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun NotificationSectionTitle(text: String) {
    Text(
        text = text,
        style = SajiTextStyles.BodyLargeBold,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun NotificationCard(item: DailyNotificationUi) {
    val borderColor = MaterialTheme.colorScheme.inversePrimary.copy(alpha = 0.55f)

    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = SajiTextStyles.BodyLargeBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.message,
                    style = SajiTextStyles.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = item.timeLabel,
                style = SajiTextStyles.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
