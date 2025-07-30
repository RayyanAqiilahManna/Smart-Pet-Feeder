package com.example.petfeeder.ui.riwayat

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R
import com.example.petfeeder.SettingsActivity
import com.example.petfeeder.network.RetrofitClient
import com.example.petfeeder.network.TokenBody
import kotlinx.coroutines.launch

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
        loadHistoryFromFirestore()
    }

    private fun loadHistoryFromFirestore() {
        val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null) ?: return

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getHistory(TokenBody(token))
                if (response.status == "success") {
                    historyList.clear()

                    // ✅ Reverse the list so newest entries appear first
                    // ✅ Optionally, you could limit results by using takeLast(30)
                    response.history
                        .reversed()
                        .forEach {
                            historyList.add(
                                FeedingHistory(
                                    date = it.date,
                                    time = it.time,
                                    portion = it.portion
                                )
                            )
                        }

                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // You can also show a Toast or Snackbar if needed
            }
        }
    }
}
