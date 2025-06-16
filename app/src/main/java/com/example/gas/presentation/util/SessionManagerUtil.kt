package com.example.gas.presentation.util

import com.google.firebase.auth.FirebaseAuth

object SessionManagerUtil {
    val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid
}