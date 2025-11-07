package com.example.sajisehat.feature.auth.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.di.AppGraph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed interface SplashNav {
    data object ToOnboarding : SplashNav
    data object ToHome : SplashNav
    data object ToLogin : SplashNav
}

class SplashViewModel(app: Application) : AndroidViewModel(app) {
    private val auth = AppGraph.authRepo
    private val prefs = AppGraph.prefs(app)

    fun decide(navigate: (SplashNav) -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            val seen = prefs.onboardingDone.first()
            // delay animasi singkat opsional
            // delay(1200)
            when {
                user != null -> navigate(SplashNav.ToHome)
                seen -> navigate(SplashNav.ToLogin)
                else -> navigate(SplashNav.ToOnboarding)
            }
        }
    }
}
