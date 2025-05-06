package com.example.petfeeder.ui.kucing

import android.content.Context
import android.content.SharedPreferences
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
import com.example.petfeeder.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
            val name = etNamaKucing.text.toString()
            val breed = dropdown.text.toString()

            if (name.isBlank() || breed.isBlank()) {
                Toast.makeText(requireContext(), "Isi semua data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = requireContext().getSharedPreferences("cats", Context.MODE_PRIVATE)
            val catList = loadCatList(prefs)
            catList.add(CatProfile(name, breed))
            saveCatList(prefs, catList)

            Toast.makeText(requireContext(), "Profil kucing $name berhasil disimpan.", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressed()
        }

        return view
    }

    private fun saveCatList(prefs: SharedPreferences, list: List<CatProfile>) {
        val json = Gson().toJson(list)
        prefs.edit().putString("cat_list", json).apply()
    }

    private fun loadCatList(prefs: SharedPreferences): MutableList<CatProfile> {
        val json = prefs.getString("cat_list", null)
        return if (json != null) {
            val type = object : TypeToken<MutableList<CatProfile>>() {}.type
            Gson().fromJson(json, type)
        } else {
            mutableListOf()
        }
    }
}