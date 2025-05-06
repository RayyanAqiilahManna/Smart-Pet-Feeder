package com.example.petfeeder.ui.penjadwalan

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.appcompat.app.AlertDialog


class ScheduleAdapter(
    private val context: Context,
    private var scheduleList: MutableList<Schedule>
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    class ScheduleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvPortion: TextView = view.findViewById(R.id.tvPortion)
        val btnDelete: Button = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = scheduleList[position]
        holder.tvTime.text = schedule.time
        holder.tvPortion.text = "Porsi: ${schedule.portion}"

        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Hapus Jadwal")
                .setMessage("Yakin ingin menghapus jadwal ${schedule.time}?")
                .setPositiveButton("YA") { _, _ ->
                    scheduleList.removeAt(position)
                    saveScheduleList()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, scheduleList.size)
                }
                .setNegativeButton("BATAL", null)
                .show()
        }

    }

    override fun getItemCount(): Int = scheduleList.size

    private fun saveScheduleList() {
        val prefs: SharedPreferences = context.getSharedPreferences("schedules", Context.MODE_PRIVATE)
        val json = Gson().toJson(scheduleList)
        prefs.edit().putString("schedule_list", json).apply()
    }

    companion object {
        fun loadScheduleList(context: Context): MutableList<Schedule> {
            val prefs = context.getSharedPreferences("schedules", Context.MODE_PRIVATE)
            val json = prefs.getString("schedule_list", null)
            return if (json != null) {
                val type = object : TypeToken<MutableList<Schedule>>() {}.type
                Gson().fromJson(json, type)
            } else {
                mutableListOf()
            }
        }
    }
}