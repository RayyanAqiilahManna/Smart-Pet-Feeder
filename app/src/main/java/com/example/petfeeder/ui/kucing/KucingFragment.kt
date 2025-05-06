package com.example.petfeeder.ui.kucing

import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class KucingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_kucing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSettings = view.findViewById<ImageView>(R.id.btnSettings)
        val btnAddCat = view.findViewById<FloatingActionButton>(R.id.btnAddKucing)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewKucing)

        // Button: go to settings
        btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        // Button: navigate to TambahKucingFragment
        btnAddCat.setOnClickListener {
            findNavController().navigate(R.id.action_KucingFragment_to_TambahKucingFragment)
        }

        // ✅ Load saved cat profiles
        val catList = CatAdapter.loadCatList(requireContext())
        Log.d("KucingFragment", "Loaded cats: $catList")
        val adapter = CatAdapter(requireContext(), catList)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
}

