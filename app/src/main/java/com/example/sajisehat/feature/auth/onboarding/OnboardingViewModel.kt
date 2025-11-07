package com.example.sajisehat.feature.auth.onboarding

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.example.sajisehat.R

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val desc: String
)

class OnboardingViewModel : ViewModel() {
    // Satu-satunya sumber data halaman
    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboard_1,
            title = "Hidup Sehat, Lebih Seru!",
            desc  = "Temukan tips sehat dan jadikan hidupmu lebih seimbang."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboard_2,
            title = "Pantau Konsumsi Harian!",
            desc  = "Cek total asupan gula harianmu dan pastikan tetap dalam batas aman."
        ),
        OnboardingPage(
            imageRes = R.drawable.onboard_3,
            title = "Kenali Gula Tersembunyi!",
            desc  = "Scan label kemasan dan ketahui kadar gula dengan cepat."
        )
    )
}
