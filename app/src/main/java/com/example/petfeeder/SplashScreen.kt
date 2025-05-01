package com.example.petfeeder

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Apply edge-to-edge padding for system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup "Masuk" button to navigate to LoginActivity
        val btnMasuk = findViewById<Button>(R.id.btnMasuk)
        btnMasuk.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Create styled, clickable text: "Belum punya akun? Daftar Sekarang"
        val tvDaftar = findViewById<TextView>(R.id.tvDaftar)
        val fullText = "Belum punya akun? Daftar Sekarang"
        val spannable = SpannableString(fullText)

        val start = fullText.indexOf("Daftar Sekarang")
        val end = start + "Daftar Sekarang".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(this@SplashScreen, RegisterActivity::class.java))
            }
        }

        // Yellow color for "Daftar Sekarang"
        spannable.setSpan(
            ForegroundColorSpan("#F4A300".toColorInt()),
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Make it clickable
        spannable.setSpan(
            clickableSpan,
            start, end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set to TextView and enable links
        tvDaftar.text = spannable
        tvDaftar.movementMethod = LinkMovementMethod.getInstance()
    }
}