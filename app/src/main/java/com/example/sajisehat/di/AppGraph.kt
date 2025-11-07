package com.example.sajisehat.di

import android.content.Context
import com.example.sajisehat.data.auth.AuthRepository
import com.example.sajisehat.data.auth.FirebaseAuthRepository
import com.example.sajisehat.data.prefs.AppPrefs

object AppGraph {
    val authRepo: AuthRepository by lazy { FirebaseAuthRepository() }
    fun prefs(context: Context) = AppPrefs(context.applicationContext)
}
