package com.example.sajisehat.navigation

import android.net.Uri

sealed class Dest(val route: String) {
    // Auth
    data object Splash : Dest("splash")
    data object Onboarding : Dest("onboarding")
    data object Login : Dest("login")
    data object LoginEmail : Dest("login/email")  

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

    data object Markah : Dest("profile/markah")
    data object Notification : Dest("profile/notification")

    data object Detail  : Dest("detail/{id}") {
        fun route(id: String) = "detail/$id"
    }
    object TrekDetail : Dest("trekDetail/{date}") {
        fun route(date: String): String = "trekDetail/$date"
    }
    data object SaveTrek : Dest("save_trek/{sugar}?name={name}") {
        fun route(sugar: Double, name: String? = null): String {
            return if (name != null) {
                "save_trek/$sugar?name=${Uri.encode(name)}"
            } else {
                "save_trek/$sugar"
            }
        }
    }
    data object SaveTrekSuccess : Dest("save_trek_success")
    object TrekManualInput : Dest("trekManualInput/{date}") {
        fun route(date: String) = "trekManualInput/$date"
    }
    object TrekManualCalc : Dest("trekManualCalc/{date}") {
        fun route(date: String) = "trekManualCalc/$date"
    }

}
