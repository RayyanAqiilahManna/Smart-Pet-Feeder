package com.example.petfeeder.ui.penjadwalan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.petfeeder.R
import com.example.petfeeder.notifications.ScheduleReceiver
import com.example.petfeeder.network.AddScheduleRequest
import com.example.petfeeder.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddScheduleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_schedule, container, false)

        val timePicker: TimePicker = view.findViewById(R.id.timePicker)
        val etPortion: EditText = view.findViewById(R.id.etPortion)
        val btnSave: Button = view.findViewById(R.id.btnSaveSchedule)

        timePicker.setIs24HourView(true)

        btnSave.setOnClickListener {
            btnSave.isEnabled = false

            val hour = timePicker.hour
            val minute = timePicker.minute
            val portionText = etPortion.text.toString()

            if (portionText.isEmpty()) {
                Toast.makeText(requireContext(), "Porsi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                btnSave.isEnabled = true
                return@setOnClickListener
            }

            val portion = portionText.toIntOrNull()
            if (portion == null || portion <= 0) {
                Toast.makeText(requireContext(), "Porsi harus berupa angka positif", Toast.LENGTH_SHORT).show()
                btnSave.isEnabled = true
                return@setOnClickListener
            }

            val timeFormatted = String.format("%02d:%02d", hour, minute)

            FirebaseAuth.getInstance().currentUser?.getIdToken(true)
                ?.addOnSuccessListener { result ->
                    val token = result.token
                    if (token != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = RetrofitClient.apiService.addSchedule(
                                    AddScheduleRequest(token, timeFormatted, portion)
                                )

                                withContext(Dispatchers.Main) {
                                    if (response.status == "success") {
                                        val schedule = Schedule(time = timeFormatted, portion = portionText)
                                        saveSchedule(schedule)
                                        setDailyAlarm(hour, minute, portionText)
                                        Toast.makeText(requireContext(), "Jadwal disimpan!", Toast.LENGTH_SHORT).show()
                                        findNavController().navigateUp()
                                    } else {
                                        Toast.makeText(requireContext(), "Gagal menyimpan ke server: ${response.message}", Toast.LENGTH_SHORT).show()
                                        btnSave.isEnabled = true
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(requireContext(), "Kesalahan jaringan: ${e.message}", Toast.LENGTH_LONG).show()
                                    btnSave.isEnabled = true
                                }
                            }
                        }
                    } else {
                        btnSave.isEnabled = true
                    }
                }
        }

        return view
    }

    private fun saveSchedule(schedule: Schedule) {
        val prefs = requireContext().getSharedPreferences("schedules", Context.MODE_PRIVATE)
        val existingJson = prefs.getString("schedule_list", null)

        val type = object : TypeToken<MutableList<Schedule>>() {}.type
        val scheduleList: MutableList<Schedule> = if (existingJson != null) {
            Gson().fromJson(existingJson, type)
        } else {
            mutableListOf()
        }

        scheduleList.add(schedule)
        val updatedJson = Gson().toJson(scheduleList)
        prefs.edit().putString("schedule_list", updatedJson).apply()
    }

    private fun setDailyAlarm(hour: Int, minute: Int, portion: String) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), ScheduleReceiver::class.java).apply {
            putExtra("time", String.format("%02d:%02d", hour, minute))
            putExtra("portion", portion)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            (hour * 60 + minute),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        // ✅ Use setExactAndAllowWhileIdle so alarm fires even in Doze mode
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
