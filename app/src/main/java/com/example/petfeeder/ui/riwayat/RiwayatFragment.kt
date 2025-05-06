package com.example.petfeeder.ui.riwayat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R
import com.example.petfeeder.SettingsActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RiwayatFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FeedingHistoryAdapter
    private lateinit var btnSettings: ImageView
    private var historyList: MutableList<FeedingHistory> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_riwayat, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewHistory)
        btnSettings = view.findViewById(R.id.btnSettings)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = FeedingHistoryAdapter(historyList)
        recyclerView.adapter = adapter

        btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    private fun loadHistory() {
        val prefs = requireContext().getSharedPreferences("feeding_history", Context.MODE_PRIVATE)
        val json = prefs.getString("history", null)
        historyList.clear()
        if (json != null) {
            val type = object : TypeToken<List<FeedingHistory>>() {}.type
            historyList.addAll(Gson().fromJson(json, type))
        }
        adapter.notifyDataSetChanged()
    }
}
