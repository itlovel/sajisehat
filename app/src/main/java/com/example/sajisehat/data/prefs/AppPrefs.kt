package com.example.sajisehat.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "app_prefs")

class AppPrefs(private val context: Context) {

    private object Keys {
        val ONBOARDING_DONE = booleanPreferencesKey("onboarding_done")
        val REMEMBER_ME     = booleanPreferencesKey("remember_me")
        val CAMERA_GRANTED  = booleanPreferencesKey("camera_granted")
    }

    val onboardingDone: Flow<Boolean> =
        context.dataStore.data.map { prefs: Preferences -> prefs[Keys.ONBOARDING_DONE] ?: false }

    val rememberMe: Flow<Boolean> =
        context.dataStore.data.map { prefs: Preferences -> prefs[Keys.REMEMBER_ME] ?: false }

    // camera
    val cameraGranted: Flow<Boolean> =
        context.dataStore.data.map { prefs: Preferences ->
            prefs[Keys.CAMERA_GRANTED] ?: false
        }

    suspend fun setOnboardingDone(done: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_DONE] = done }
    }

    suspend fun setRememberMe(v: Boolean) {
        context.dataStore.edit { it[Keys.REMEMBER_ME] = v }
    }

    suspend fun setCameraGranted(granted: Boolean) {
        context.dataStore.edit { it[Keys.CAMERA_GRANTED] = granted }
    }

}
