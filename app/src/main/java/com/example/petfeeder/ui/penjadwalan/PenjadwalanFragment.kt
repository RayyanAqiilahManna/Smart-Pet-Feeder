package com.example.petfeeder.ui.penjadwalan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfeeder.R
import com.example.petfeeder.SettingsActivity
import com.example.petfeeder.network.ApiService
import com.example.petfeeder.network.RetrofitClient
import com.example.petfeeder.network.TokenBody
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PenjadwalanFragment : Fragment() {

    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: FloatingActionButton
    private lateinit var btnSettings: ImageView
    private val apiService = RetrofitClient.apiService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_penjadwalan, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewSchedules)
        addButton = view.findViewById(R.id.btnAddSchedule)
        btnSettings = view.findViewById(R.id.btnSettings)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

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
        loadSchedulesFromServer()
    }

    private fun loadSchedulesFromServer() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.getIdToken(true)?.addOnSuccessListener { result ->
            val token = result.token ?: return@addOnSuccessListener

            lifecycleScope.launch {
                try {
                    val response = apiService.getSchedules(TokenBody(token))
                    if (response.status == "success") {
                        // Convert List<ScheduleDto> to List<ScheduleWithId> using adapter helper
                        val schedules = ScheduleAdapter.fromDtoList(response.schedules)

                        scheduleAdapter = ScheduleAdapter(requireContext(), schedules)
                        recyclerView.adapter = scheduleAdapter
                    } else {
                        Toast.makeText(requireContext(), "Gagal memuat jadwal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Terjadi kesalahan: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }?.addOnFailureListener {
            Toast.makeText(requireContext(), "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
        }
    }
}