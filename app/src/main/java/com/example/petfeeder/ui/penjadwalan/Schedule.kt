package com.example.petfeeder.ui.penjadwalan

data class Schedule(
    val id: String = "", // For Firestore doc ID
    val time: String,
    val portion: String
)