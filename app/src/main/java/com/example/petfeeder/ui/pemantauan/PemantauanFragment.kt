package com.example.petfeeder.ui.pemantauan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.petfeeder.R
import com.example.petfeeder.SettingsActivity
import com.example.petfeeder.network.RetrofitClient
import com.example.petfeeder.network.TokenBody
import com.example.petfeeder.network.TriggerRequest
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class PemantauanFragment : Fragment() {

    private lateinit var tvStorageValue: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private val refreshHandler = Handler(Looper.getMainLooper())
    private val refreshInterval = 5_000L // 5 seconds

    private val refreshRunnable = object : Runnable {
        override fun run() {
            fetchStoragePercentage()
            refreshHandler.postDelayed(this, refreshInterval)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pemantauan, container, false)

        tvStorageValue = view.findViewById(R.id.tvStorageValue)
        firebaseAuth = FirebaseAuth.getInstance()

        // Open settings
        val settingsIcon = view.findViewById<ImageView>(R.id.btnSettings)
        settingsIcon.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }

        // Load Live Stream into WebView
        val webLiveCam = view.findViewById<WebView>(R.id.webLiveCam)
        webLiveCam.settings.javaScriptEnabled = true
        webLiveCam.settings.loadWithOverviewMode = true
        webLiveCam.settings.useWideViewPort = true
        webLiveCam.settings.domStorageEnabled = true
        webLiveCam.webViewClient = WebViewClient()

        // Replace this with your actual backend stream URL
        val streamUrl = "https://spf-backend-566730574934.us-central1.run.app/stream" // ← or your Cloud Run IP/Domain
        webLiveCam.loadUrl(streamUrl)

        // Button: Mulai Pemantauan
        val btnMulaiPemantauan = view.findViewById<Button>(R.id.btnMulaiPemantauan)
        btnMulaiPemantauan.setOnClickListener {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.getIdToken(true)
                    .addOnSuccessListener { result ->
                        val token = result.token
                        if (token != null) {
                            lifecycleScope.launch {
                                try {
                                    val response = RetrofitClient.apiService.triggerDetection(TriggerRequest(token))
                                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                                } catch (e: Exception) {
                                    Toast.makeText(requireContext(), "Gagal trigger deteksi", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Gagal mendapatkan token", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Pengguna belum login", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        refreshHandler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        refreshHandler.removeCallbacks(refreshRunnable)
    }

    private fun fetchStoragePercentage() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            user.getIdToken(true)
                .addOnSuccessListener { result ->
                    val token = result.token
                    if (token != null) {
                        lifecycleScope.launch {
                            try {
                                val response = RetrofitClient.apiService.getStorage(TokenBody(token))
                                val percentText = "${response.percent_full.toInt()}%"
                                tvStorageValue.text = percentText
                            } catch (e: Exception) {
                                tvStorageValue.text = "--%"
                                Toast.makeText(
                                    requireContext(),
                                    "Gagal memuat data penyimpanan",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    tvStorageValue.text = "--%"
                    Toast.makeText(requireContext(), "Token Firebase tidak valid", Toast.LENGTH_SHORT).show()
                }
        } else {
            tvStorageValue.text = "--%"
            Toast.makeText(requireContext(), "Pengguna belum login", Toast.LENGTH_SHORT).show()
        }
    }
}
