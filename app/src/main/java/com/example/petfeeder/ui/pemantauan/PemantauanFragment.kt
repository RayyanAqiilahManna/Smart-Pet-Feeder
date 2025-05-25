package com.example.petfeeder.ui.pemantauan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.petfeeder.R
import com.example.petfeeder.SettingsActivity

class PemantauanFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pemantauan, container, false)

        // Open settings
        val settingsIcon = view.findViewById<ImageView>(R.id.btnSettings)
        settingsIcon.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // Button: Mulai Pemantauan
        val btnMulaiPemantauan = view.findViewById<Button>(R.id.btnMulaiPemantauan)
        btnMulaiPemantauan.setOnClickListener {
            Toast.makeText(requireContext(), "Pemantauan dimulai!", Toast.LENGTH_SHORT).show()
            // TODO: Insert logic to start live monitoring / video stream etc.
        }

        return view
    }
}