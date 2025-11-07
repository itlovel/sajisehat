package com.example.sajisehat.navigation

sealed class Dest(val route: String) {
    // Auth
    data object Splash : Dest("splash")
    data object Onboarding : Dest("onboarding")
    data object Login : Dest("login")
    data object RegisterName : Dest("register/name")
    data object RegisterEmail : Dest("register/email")
    data object RegisterPassword : Dest("register/password")
    data object RegisterSuccess : Dest("register/success")

    data object Home    : Dest("home")
    data object Trek    : Dest("trek")
    data object Scan    : Dest("scan")     // tombol tengah (FAB)
    data object Catalog : Dest("catalog")
    data object Profile : Dest("profile")

    data object Detail  : Dest("detail/{id}") {
        fun route(id: String) = "detail/$id"
    }
}

