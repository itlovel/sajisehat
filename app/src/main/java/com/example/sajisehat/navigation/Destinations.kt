package com.example.sajisehat.navigation

sealed class Dest(val route: String) {
    data object Home    : Dest("home")
    data object Trek    : Dest("trek")
    data object Scan    : Dest("scan")     // tombol tengah (FAB)
    data object Catalog : Dest("catalog")
    data object Profile : Dest("profile")

    data object Detail  : Dest("detail/{id}") {
        fun route(id: String) = "detail/$id"
    }
}

