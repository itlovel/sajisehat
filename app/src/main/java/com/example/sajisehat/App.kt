package com.example.sajisehat

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sajisehat.feature.catalog.ui.CatalogScreen
import com.example.sajisehat.feature.detail.ui.DetailScreen
import com.example.sajisehat.feature.home.ui.HomeScreen
import com.example.sajisehat.feature.profile.ui.ProfileScreen
import com.example.sajisehat.feature.scan.ui.ScanScreen
import com.example.sajisehat.feature.trek.ui.TrekScreen
import com.example.sajisehat.navigation.Dest
import com.example.sajisehat.navigation.isOnRoute
import com.example.sajisehat.navigation.navigateSingleTopTo
import com.example.sajisehat.ui.components.bottombar.AppBottomBar
import com.example.sajisehat.ui.components.bottombar.BottomNavItem
import com.example.sajisehat.ui.components.bottombar.CenterScanFab
import com.example.sajisehat.ui.components.bottombar.rememberBottomBarSpec

@Composable
fun SajisehatApp() {
    val nav = rememberNavController()
    val spec = rememberBottomBarSpec() // responsive sizes (bar, icons, FAB, spacer, etc.)

    // Bottom bar items (left side)
    val leftItems = listOf(
        BottomNavItem(
            route = Dest.Home.route, label = "Beranda",
            iconRes = R.drawable.ic_home_outline,
            iconSelectedRes = R.drawable.ic_home_filled,
            useTint = false
        ),
        BottomNavItem(
            route = Dest.Trek.route, label = "Trek Gula",
            iconRes = R.drawable.ic_trek_outline,
            iconSelectedRes = R.drawable.ic_trek_filled,
            useTint = false
        )
    )

    // Bottom bar items (right side)
    val rightItems = listOf(
        BottomNavItem(
            route = Dest.Catalog.route, label = "Katalog",
            iconRes = R.drawable.ic_catalog_outline,
            iconSelectedRes = R.drawable.ic_catalog_filled,
            useTint = false
        ),
        BottomNavItem(
            route = Dest.Profile.route, label = "Profil",
            iconRes = R.drawable.ic_profile_outline,
            iconSelectedRes = R.drawable.ic_profile_filled,
            useTint = false
        )
    )

    // Routes where the bottom bar should be shown
    val barRoutes = setOf(
        Dest.Home.route, Dest.Trek.route, Dest.Scan.route, Dest.Catalog.route, Dest.Profile.route
    )

    val backStackEntry by nav.currentBackStackEntryAsState()
    val currentDest = backStackEntry?.destination
    val showBar = currentDest?.hierarchy?.any { it.route in barRoutes } == true
    val scanSelected = currentDest.isOnRoute(Dest.Scan.route)

    Scaffold(
        bottomBar = {
            if (showBar) AppBottomBar(
                nav = nav,
                leftItems = leftItems,
                rightItems = rightItems,
                barHeight = spec.barHeight,
                corner = 8.dp,          // hanya sudut atas yang melengkung (bawah 0.dp di file bar)
                haloSize = spec.haloSize,
                iconSize = spec.iconSize,
                spacerWidth = spec.spacerWidth,
                showLabels = spec.showLabels,
                centerLabel = "Scan",
                centerSelected = scanSelected,
                onCenterClick = { nav.navigateSingleTopTo(Dest.Scan.route) }
            )
        },
        floatingActionButton = {
            if (showBar) CenterScanFab(
                selected = scanSelected,
                icon = painterResource(R.drawable.ic_scan),
                onClick = { nav.navigateSingleTopTo(Dest.Scan.route) },
                modifier = Modifier.offset(y = spec.fabOffsetY + 15.dp), // ← FAB lebih turun
                size = spec.fabSize,
                iconSize = spec.fabIconSize
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Dest.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(Dest.Home.route)    { HomeScreen(onOpen = { id -> nav.navigate(Dest.Detail.route(id)) }) }
            composable(Dest.Trek.route)    { TrekScreen() }
            composable(Dest.Scan.route)    { ScanScreen() }
            composable(Dest.Catalog.route) { CatalogScreen() }
            composable(Dest.Profile.route) { ProfileScreen() }
            composable(Dest.Detail.route)  { back ->
                val id = back.arguments?.getString("id").orEmpty()
                DetailScreen(id)
            }
        }
    }
}
