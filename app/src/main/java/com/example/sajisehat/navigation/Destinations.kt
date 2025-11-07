package com.example.sajisehat.navigation

// navigation/Dest.kt
sealed class Dest(val route: String) {
    // Auth
    data object Splash : Dest("splash")
    data object Onboarding : Dest("onboarding")
    data object Login : Dest("login")

    // Parent graph untuk register
    data object Register : Dest("register")

    data object RegisterName : Dest("register/name")
    data object RegisterEmail : Dest("register/email")
    data object RegisterPassword : Dest("register/password")
    data object RegisterSuccess : Dest("register/success")

    // Main
    data object Home    : Dest("home")
    data object Trek    : Dest("trek")
    data object Scan    : Dest("scan")
    data object Catalog : Dest("catalog")
    data object Profile : Dest("profile")

    data object Detail  : Dest("detail/{id}") {
        fun route(id: String) = "detail/$id"
    }
}

