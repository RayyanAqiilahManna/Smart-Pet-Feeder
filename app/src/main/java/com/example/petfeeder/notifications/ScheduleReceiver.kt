package com.example.petfeeder.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.petfeeder.R
import com.example.petfeeder.network.AddHistoryRequest
import com.example.petfeeder.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ScheduleReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val time = intent.getStringExtra("time") ?: "Unknown Time"
        val portion = intent.getStringExtra("portion") ?: "Unknown Portion"
        val currentDate = SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(Date())

        Log.d("ScheduleReceiver", "📅 Triggered: time=$time, portion=$portion")

        // ✅ Use goAsync so the BroadcastReceiver won’t die mid-network call
        val pendingResult = goAsync()

        // ✅ Fetch a fresh Firebase token
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val firebaseUser = FirebaseAuth.getInstance().currentUser
                if (firebaseUser != null) {
                    val tokenResult = firebaseUser.getIdToken(true).await()
                    val token = tokenResult.token

                    if (token != null) {
                        val response = RetrofitClient.apiService.addHistory(
                            AddHistoryRequest(
                                token = token,
                                date = currentDate,
                                time = time,
                                portion = portion
                            )
                        )
                        Log.d("ScheduleReceiver", "✅ History saved: ${response.message}")
                    } else {
                        Log.e("ScheduleReceiver", "❌ Token is null - cannot save history")
                    }
                } else {
                    Log.e("ScheduleReceiver", "❌ Firebase user is null - cannot save history")
                }
            } catch (e: Exception) {
                Log.e("ScheduleReceiver", "❌ Error saving history: ${e.message}", e)
            } finally {
                pendingResult.finish()
            }
        }

        // 🔔 Show notification
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
                Log.d("ScheduleReceiver", "🔔 Notification shown")
            }
        } else {
            Log.w("ScheduleReceiver", "⚠️ Notification permission not granted")
        }

        // ✅ Reschedule the alarm for the next day
        rescheduleNextDay(context, time, portion)
    }

    private fun rescheduleNextDay(context: Context, time: String, portion: String) {
        try {
            val (hour, minute) = time.split(":").map { it.toInt() }

            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                add(Calendar.DATE, 1)
            }

            val intent = Intent(context, ScheduleReceiver::class.java).apply {
                putExtra("time", time)
                putExtra("portion", portion)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                (hour * 60 + minute),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )

            Log.d("ScheduleReceiver", "⏰ Rescheduled alarm for next day: $time")
        } catch (e: Exception) {
            Log.e("ScheduleReceiver", "❌ Failed to reschedule: ${e.message}", e)
        }
    }
}
