package com.example.sajisehat

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.sajisehat.feature.auth.login.LoginEmailScreen
import com.example.sajisehat.feature.auth.login.LoginScreen
import com.example.sajisehat.feature.auth.onboarding.OnboardingScreen
import com.example.sajisehat.feature.auth.register.RegisterEmailScreen
import com.example.sajisehat.feature.auth.register.RegisterNameScreen
import com.example.sajisehat.feature.auth.register.RegisterPasswordScreen
import com.example.sajisehat.feature.auth.register.RegisterSuccessScreen
import com.example.sajisehat.feature.auth.register.RegisterViewModel
import com.example.sajisehat.feature.auth.splash.SplashNav
import com.example.sajisehat.feature.auth.splash.SplashScreen
import com.example.sajisehat.feature.catalog.ui.CatalogScreen
import com.example.sajisehat.feature.detail.ui.DetailScreen
import com.example.sajisehat.feature.home.ui.HomeScreen
import com.example.sajisehat.feature.profile.ui.ProfileScreen
import com.example.sajisehat.feature.scan.ScanRoute
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
    val spec = rememberBottomBarSpec()

    val leftItems = listOf(
        BottomNavItem(
            route = Dest.Home.route, label = "Beranda",
            iconRes = R.drawable.ic_home_outline, iconSelectedRes = R.drawable.ic_home_filled, useTint = false
        ),
        BottomNavItem(
            route = Dest.Trek.route, label = "Trek Gula",
            iconRes = R.drawable.ic_trek_outline, iconSelectedRes = R.drawable.ic_trek_filled, useTint = false
        )
    )
    val rightItems = listOf(
        BottomNavItem(
            route = Dest.Catalog.route, label = "Katalog",
            iconRes = R.drawable.ic_catalog_outline, iconSelectedRes = R.drawable.ic_catalog_filled, useTint = false
        ),
        BottomNavItem(
            route = Dest.Profile.route, label = "Profil",
            iconRes = R.drawable.ic_profile_outline, iconSelectedRes = R.drawable.ic_profile_filled, useTint = false
        )
    )

    val barRoutes = setOf(Dest.Home.route, Dest.Trek.route, Dest.Scan.route, Dest.Catalog.route, Dest.Profile.route)
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
                corner = 8.dp,
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
                modifier = Modifier.offset(y = spec.fabOffsetY + 15.dp),
                size = spec.fabSize,
                iconSize = spec.fabIconSize
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { inner ->
        NavHost(
            navController = nav,
            startDestination = Dest.Splash.route,
            route = "root",
            modifier = Modifier.padding(inner)
        ) {
            // ---------- AUTH ----------
            composable(Dest.Splash.route) {
                SplashScreen(onNav = { navDir ->
                    when (navDir) {
                        is SplashNav.ToOnboarding -> nav.navigate(Dest.Onboarding.route) {
                            popUpTo(Dest.Splash.route) { inclusive = true }
                        }
                        is SplashNav.ToHome -> nav.navigate(Dest.Home.route) {
                            popUpTo(Dest.Splash.route) { inclusive = true }
                        }
                        is SplashNav.ToLogin -> nav.navigate(Dest.Login.route) {
                            popUpTo(Dest.Splash.route) { inclusive = true }
                        }
                    }
                })
            }
            composable(Dest.Onboarding.route) {
                OnboardingScreen(onFinish = {
                    nav.navigate(Dest.Login.route) {
                        popUpTo(Dest.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Dest.Login.route) {
                LoginScreen(
                    onRegister = { nav.navigate(Dest.Register.route) },
                    onLoggedIn = { nav.navigate(Dest.Home.route) { popUpTo(0) } },
                    onEmailSignIn = { nav.navigate(Dest.LoginEmail.route) }
                )
            }
            composable(Dest.LoginEmail.route) {
                LoginEmailScreen(
                    onLoggedIn = { nav.navigate(Dest.Home.route) { popUpTo(0) } }
                )
            }

            // ---------- REGISTER GRAPH (shared RegisterViewModel) ----------
            navigation(startDestination = Dest.RegisterName.route, route = Dest.Register.route) {
                registerGraph(nav)
            }

            // ---------- MAIN ----------
            composable(Dest.Home.route)    { HomeScreen(onOpen = { id -> nav.navigate(Dest.Detail.route(id)) }) }
            composable(Dest.Trek.route)    { TrekScreen() }
            composable(Dest.Scan.route) { ScanRoute(navController = nav) }
            composable(Dest.Catalog.route) { CatalogScreen() }
            composable(Dest.Profile.route) { ProfileScreen() }
            composable(Dest.Detail.route)  { back ->
                val id = back.arguments?.getString("id").orEmpty()
                DetailScreen(id)
            }
        }
    }
}

private fun NavGraphBuilder.registerGraph(nav: NavHostController) {
    composable(Dest.RegisterName.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            nav.getBackStackEntry(Dest.Register.route)
        }
        val vm = ViewModelProvider(parentEntry)[RegisterViewModel::class.java]
        RegisterNameScreen(vm = vm, onNext = { nav.navigate(Dest.RegisterEmail.route) })
    }

    composable(Dest.RegisterEmail.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            nav.getBackStackEntry(Dest.Register.route)
        }
        val vm = ViewModelProvider(parentEntry)[RegisterViewModel::class.java]
        RegisterEmailScreen(vm = vm, onNext = { nav.navigate(Dest.RegisterPassword.route) })
    }

    composable(Dest.RegisterPassword.route) { backStackEntry ->
        val parentEntry = remember(backStackEntry) {
            nav.getBackStackEntry(Dest.Register.route)
        }
        val vm = ViewModelProvider(parentEntry)[RegisterViewModel::class.java]
        RegisterPasswordScreen(vm = vm, onSuccess = {
            nav.navigate(Dest.RegisterSuccess.route)
        })
    }

    composable(Dest.RegisterSuccess.route) {
        RegisterSuccessScreen(onGoHome = {
            nav.navigate(Dest.Home.route) { popUpTo(0) }
        })
    }
}
