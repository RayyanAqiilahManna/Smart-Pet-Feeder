package com.example.petfeeder.notifications

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.petfeeder.R
import com.example.petfeeder.ui.riwayat.FeedingHistory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class ScheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val time = intent.getStringExtra("time") ?: "Unknown Time"
        val portion = intent.getStringExtra("portion") ?: "Unknown Portion"

        // Save to history
        val prefs = context.getSharedPreferences("feeding_history", Context.MODE_PRIVATE)
        val gson = Gson()
        val type = object : TypeToken<MutableList<FeedingHistory>>() {}.type
        val existing = gson.fromJson<MutableList<FeedingHistory>>(prefs.getString("history", null), type) ?: mutableListOf()

        val currentDate = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())
        existing.add(0, FeedingHistory(currentDate, time, portion))

        prefs.edit().putString("history", gson.toJson(existing)).apply()

        // Show notification if permission is granted
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val builder = NotificationCompat.Builder(context, "schedule_channel")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Mesin Mengeluarkan Makanan")
                .setContentText("Jadwal: $time, Porsi: $portion gr")
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(context)) {
                val notificationId = (0..999999).random()
                notify(notificationId, builder.build())
            }
        }
    }
}
