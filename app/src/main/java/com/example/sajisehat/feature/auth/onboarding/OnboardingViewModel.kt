package com.example.sajisehat.feature.auth.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.di.AppGraph
import kotlinx.coroutines.launch

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = AppGraph.prefs(app)
    fun setDone(done: Boolean) = viewModelScope.launch { prefs.setOnboardingDone(done) }
}

data class OnboardingPage(
    val imageRes: Int,
    val title: String,
    val body: String
)
