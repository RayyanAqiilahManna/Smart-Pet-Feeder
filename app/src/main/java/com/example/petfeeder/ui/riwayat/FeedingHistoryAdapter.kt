package com.example.petfeeder.ui.riwayat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R

class FeedingHistoryAdapter(private val historyList: List<FeedingHistory>) :
    RecyclerView.Adapter<FeedingHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvHistoryDate)
        val tvTime: TextView = view.findViewById(R.id.tvHistoryTime)
        val tvPortion: TextView = view.findViewById(R.id.tvHistoryPortion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_feeding_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.tvDate.text = item.date
        holder.tvTime.text = item.time
        holder.tvPortion.text = "Porsi ${item.portion}g"
    }

    override fun getItemCount(): Int = historyList.size
}