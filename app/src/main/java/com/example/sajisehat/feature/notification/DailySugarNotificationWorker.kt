// feature/notification/DailySugarNotificationWorker.kt
package com.example.sajisehat.feature.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.WorkManager
import com.example.sajisehat.R
import com.example.sajisehat.di.AppGraph
import com.example.sajisehat.feature.trek.model.getDailySugarLevel
import java.time.LocalDate

private const val CHANNEL_ID = "daily_sugar"

class DailySugarNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        val auth = AppGraph.authRepo
        val trek = AppGraph.trekRepository

        val user = auth.currentUser ?: return Result.success()

        val today = LocalDate.now()
        val total = trek.getTotalSugarForDate(user.uid, today)

        val level = getDailySugarLevel(total)

        val vm = NotificationViewModel(trekRepository = trek, authRepository = auth)
        val title = "Konsumsi gula hari ini"
        val text  = "Total gulamu hari ini %.0f gram.".format(total)

        createChannel()

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_trek_filled)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext)
            .notify(today.toEpochDay().toInt(), notification)

        return Result.success()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notifikasi Gula Harian",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            nm.createNotificationChannel(channel)
        }
    }
}
