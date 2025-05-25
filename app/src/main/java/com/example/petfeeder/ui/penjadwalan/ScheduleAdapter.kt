package com.example.petfeeder.ui.penjadwalan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R
import com.example.petfeeder.network.ApiService
import com.example.petfeeder.network.DeleteScheduleRequest
import com.example.petfeeder.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ScheduleWithId(
    val id: String,
    val time: String,
    val portion: String
)

class ScheduleAdapter(
    private val context: Context,
    private var scheduleList: MutableList<ScheduleWithId>
) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private val apiService: ApiService = RetrofitClient.apiService

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
                    deleteSchedule(schedule.id, position)
                }
                .setNegativeButton("BATAL", null)
                .show()
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    private fun deleteSchedule(scheduleId: String, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val token = FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.await()?.token
                if (token == null) {
                    Toast.makeText(context, "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val response = apiService.deleteSchedule(
                    DeleteScheduleRequest(token = token, schedule_id = scheduleId)
                )

                if (response.status == "success") {
                    scheduleList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, scheduleList.size)
                    Toast.makeText(context, "Jadwal dihapus", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Gagal menghapus jadwal", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        fun fromDtoList(dtoList: List<com.example.petfeeder.network.ScheduleDto>): MutableList<ScheduleWithId> {
            return dtoList.map {
                ScheduleWithId(id = it.id, time = it.time, portion = it.portion.toString())
            }.toMutableList()
        }
    }
}
