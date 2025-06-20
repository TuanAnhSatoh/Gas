package com.example.gas.data.source.network.datasource

import com.example.gas.data.source.network.model.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class UserFirebaseDataSource @Inject constructor(
    firebaseDatabase: FirebaseDatabase,
    private val firebaseAuth: FirebaseAuth
) : UserDataSource {

    private val usersRef = firebaseDatabase.getReference("users")

    override suspend fun saveUser(user: FirebaseUser) {
        try {
            withContext(Dispatchers.IO) {
                usersRef.child(user.userId).setValue(user).await()
                Timber.tag("Firebase").d("Saved user with UID ${user.userId}: $user")
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to save user with UID ${user.userId}")
            throw Exception("Cannot save user with UID ${user.userId}: ${e.message}")
        }
    }

    override suspend fun loadUser(uid: String): FirebaseUser? {
        return try {
            withContext(Dispatchers.IO) {
                val snapshot = usersRef.child(uid).get().await()
                snapshot.getValue(FirebaseUser::class.java).also { user ->
                    if (user == null) {
                        Timber.tag("Firebase").w("User with UID $uid not found")
                    } else {
                        Timber.tag("Firebase").d("Loaded user with UID $uid: $user")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to load user with UID $uid")
            throw Exception("Cannot load user with UID $uid: ${e.message}")
        }
    }

    override suspend fun updateUser(uid: String, user: FirebaseUser) {
        try {
            withContext(Dispatchers.IO) {
                usersRef.child(uid).setValue(user).await()
                Timber.tag("Firebase").d("Updated user with UID $uid: $user")
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to update user with UID $uid")
            throw Exception("Cannot update user with UID $uid: ${e.message}")
        }
    }

    override suspend fun deleteUser(uid: String) {
        try {
            withContext(Dispatchers.IO) {
                usersRef.child(uid).removeValue().await()
                Timber.tag("Firebase").d("Deleted user with UID $uid")
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to delete user with UID $uid")
            throw Exception("Cannot delete user with UID $uid: ${e.message}")
        }
    }

    override suspend fun getEmailByUid(uid: String): String? {
        return try {
            withContext(Dispatchers.IO) {
                val currentUser = firebaseAuth.currentUser
                val email = if (currentUser != null && currentUser.uid == uid) {
                    currentUser.email
                } else {
                    null
                }
                if (email == null) {
                    Timber.tag("Firebase").w("Email not found for UID $uid")
                } else {
                    Timber.tag("Firebase").d("Found email $email for UID $uid")
                }
                email
            }
        } catch (e: Exception) {
            Timber.tag("Firebase").e(e, "Failed to get email for UID $uid")
            throw Exception("Cannot get email for UID $uid: ${e.message}")
        }
    }
}