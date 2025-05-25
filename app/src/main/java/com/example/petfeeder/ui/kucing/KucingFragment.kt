package com.example.petfeeder.ui.kucing

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.petfeeder.network.RetrofitClient
import com.example.petfeeder.network.TokenBody
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class KucingFragment : Fragment() {

    private var token: String? = null
    private lateinit var adapter: CatAdapter
    private lateinit var recyclerView: RecyclerView
    private val catList = mutableListOf<CatProfile>()

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
        recyclerView = view.findViewById(R.id.recyclerViewKucing)

        btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        btnAddCat.setOnClickListener {
            findNavController().navigate(R.id.action_KucingFragment_to_TambahKucingFragment)
        }

        adapter = CatAdapter(requireContext(), catList) { catProfile ->
            deleteCatProfile(catProfile)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        fetchUserTokenAndLoadCats()
    }

    private fun fetchUserTokenAndLoadCats() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }
        user.getIdToken(true).addOnSuccessListener { result ->
            token = result.token
            token?.let {
                loadCatProfiles(it)
            }
        }.addOnFailureListener { e ->
            Toast.makeText(requireContext(), "Failed to get token: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCatProfiles(token: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getCatProfiles(TokenBody(token))
                if (response.status == "success") {
                    catList.clear()
                    // Map from DTO to CatProfile data class (include id)
                    catList.addAll(response.cats.map { CatProfile(it.id, it.name, it.breed) })
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(requireContext(), "Failed to load cats", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("KucingFragment", "loadCatProfiles", e)
            }
        }
    }

    private fun deleteCatProfile(catProfile: CatProfile) {
        val currentToken = token
        if (currentToken == null) {
            Toast.makeText(requireContext(), "Token not available", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.deleteCatProfile(
                    com.example.petfeeder.network.DeleteCatProfileRequest(currentToken, catProfile.id)
                )
                if (response.status == "success") {
                    catList.remove(catProfile)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(requireContext(), "Deleted ${catProfile.name}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete cat profile", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error deleting cat: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("KucingFragment", "deleteCatProfile", e)
            }
        }
    }
}