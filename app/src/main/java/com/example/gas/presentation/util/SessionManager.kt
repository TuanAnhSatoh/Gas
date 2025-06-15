package com.example.gas.presentation.util

import com.google.firebase.auth.FirebaseAuth

object SessionManager {
    val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
}