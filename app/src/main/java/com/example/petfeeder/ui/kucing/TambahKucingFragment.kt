package com.example.petfeeder.ui.kucing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.petfeeder.R
import com.example.petfeeder.network.AddCatProfileRequest
import com.example.petfeeder.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TambahKucingFragment : Fragment() {

    private val jenisKucingList = listOf(
        "Abyssinian", "American Shorthair", "Bengal", "Birman", "Bombay",
        "British Shorthair", "Egyptian Mau", "Maine Coon", "Persian", "Ragdoll",
        "Russian Blue", "Scottish Fold", "Siamese", "Sphynx"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_tambah_kucing, container, false)

        val etNamaKucing = view.findViewById<EditText>(R.id.etNamaKucing)
        val dropdown = view.findViewById<AutoCompleteTextView>(R.id.dropdownJenisKucing)
        val btnSimpan = view.findViewById<Button>(R.id.btnSimpan)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, jenisKucingList)
        dropdown.setAdapter(adapter)

        btnSimpan.setOnClickListener {
            val name = etNamaKucing.text.toString().trim()
            val breed = dropdown.text.toString().trim()

            if (name.isBlank() || breed.isBlank()) {
                Toast.makeText(requireContext(), "Isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addCatProfileToBackend(name, breed)
        }

        return view
    }

    private fun addCatProfileToBackend(name: String, breed: String) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        user.getIdToken(true).addOnSuccessListener { result ->
            val token = result.token
            if (token == null) {
                Toast.makeText(requireContext(), "Failed to get token", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.apiService.addCatProfile(
                        AddCatProfileRequest(token, name, breed)
                    )
                    if (response.status == "success") {
                        Toast.makeText(requireContext(), "Profil kucing $name berhasil disimpan.", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressed()
                    } else {
                        Toast.makeText(requireContext(), "Gagal menyimpan profil: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to get token: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
