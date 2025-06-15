package com.example.gas.data.source.network.model

import com.example.gas.domain.model.Gender

data class FirebaseUser(
    var userId: String = "",
    var password: String = "",
    var name: String = "",
    var address: String? = null,
    var dateOfBirth: String = "",
    var gender: Gender = Gender.None,
    var phone: String = "",
    var createdAt: String = "",
    var updatedAt: String = ""
)