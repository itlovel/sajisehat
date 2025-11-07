package com.example.sajisehat.feature.auth.onboarding

import android.app.Application
import androidx.annotation.DrawableRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sajisehat.R
import com.example.sajisehat.di.AppGraph
import kotlinx.coroutines.launch

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val desc: String
)

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {

    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboard_1,
            title = "Kenali Gula Tersembunyi!",
            desc  = "Scan label kemasan dan ketahui kadar gula di makanan dan minumanmu dengan cepat."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboard_2,
            title = "Pantau Konsumsi Harian!",
            desc  = "Cek total asupan gula harianmu dan pastikan tetap dalam batas aman."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboard_3,
            title = "Hidup Sehat, Lebih Seru!",
            desc  = "Temukan tips sehat dan jadikan hidupmu lebih seimbang."
        )
    )

    // Tandai onboarding selesai → Splash akan skip
    fun completeOnboarding() = viewModelScope.launch {
        AppGraph.prefs(getApplication()).setOnboardingDone(true)
    }
}
