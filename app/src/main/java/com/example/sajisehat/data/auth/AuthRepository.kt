package com.example.sajisehat.data.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    val authState: Flow<FirebaseUser?>  // emit saat login/logout

    suspend fun signInEmail(email: String, password: String): Result<FirebaseUser>
    suspend fun registerEmail(email: String, password: String, displayName: String): Result<FirebaseUser>
    suspend fun signOut()

    // Opsional: Google Sign-In jika kamu pakai
    suspend fun signInWithCredential(credential: AuthCredential): Result<FirebaseUser>
}
