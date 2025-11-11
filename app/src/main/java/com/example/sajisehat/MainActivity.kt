package com.example.sajisehat

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sajisehat.ui.theme.AppTheme
import androidx.activity.compose.setContent
import com.example.sajisehat.feature.notification.DailySugarNotificationWorker
import com.example.sajisehat.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppTheme { SajisehatApp() } }
    }
}

fun scheduleDailySugarWorker(context: Context) {
    val now = java.time.LocalDateTime.now()
    val targetToday = now.withHour(21).withMinute(0).withSecond(0).withNano(0)
    val firstRun = if (now.isBefore(targetToday)) targetToday else targetToday.plusDays(1)
    val delayMillis = java.time.Duration.between(now, firstRun).toMillis()

    val request = androidx.work.PeriodicWorkRequestBuilder<DailySugarNotificationWorker>(
        24, java.util.concurrent.TimeUnit.HOURS
    )
        .setInitialDelay(delayMillis, java.util.concurrent.TimeUnit.MILLISECONDS)
        .build()

    androidx.work.WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "daily_sugar_notification",
        androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
        request
    )
}

