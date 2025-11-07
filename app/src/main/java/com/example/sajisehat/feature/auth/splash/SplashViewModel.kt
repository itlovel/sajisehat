package com.example.sajisehat.feature.auth.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.di.AppGraph
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface SplashNav {
    data object ToOnboarding : SplashNav
    data object ToHome : SplashNav
    data object ToLogin : SplashNav
}

class SplashViewModel(app: Application) : AndroidViewModel(app) {

    private val auth = AppGraph.authRepo
    private val prefs = AppGraph.prefs(app)

    fun decide(navigate: (SplashNav) -> Unit, minSplashMillis: Long = 2000L) {
        viewModelScope.launch {
            val seen = withContext(Dispatchers.IO) { prefs.onboardingDone.first() }
            val user = auth.currentUser

            delay(minSplashMillis)

            when {
                user != null -> navigate(SplashNav.ToHome)
                seen -> navigate(SplashNav.ToLogin)
                else -> navigate(SplashNav.ToOnboarding)
            }
        }
    }
}
