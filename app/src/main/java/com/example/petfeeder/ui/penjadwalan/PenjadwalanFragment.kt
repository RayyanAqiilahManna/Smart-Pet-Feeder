package com.example.petfeeder.ui.penjadwalan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R
import com.example.petfeeder.SettingsActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PenjadwalanFragment : Fragment() {

    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var btnSettings: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_penjadwalan, container, false)

        // ✅ Fixed: match the actual ID from XML
        recyclerView = view.findViewById(R.id.recyclerViewSchedules)
        addButton = view.findViewById(R.id.btnAddSchedule)
        btnSettings = view.findViewById(R.id.btnSettings)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load saved schedules
        val scheduleList = ScheduleAdapter.loadScheduleList(requireContext())
        scheduleAdapter = ScheduleAdapter(requireContext(), scheduleList)
        recyclerView.adapter = scheduleAdapter

        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_penjadwalanFragment_to_addScheduleFragment)
        }

        btnSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Reload the latest saved schedules when returning to this fragment
        val updatedList = ScheduleAdapter.loadScheduleList(requireContext())
        scheduleAdapter = ScheduleAdapter(requireContext(), updatedList)
        recyclerView.adapter = scheduleAdapter
    }
}
