package com.example.sajisehat

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sajisehat.di.AppGraph
import com.example.sajisehat.feature.trek.TrekViewModel
import com.example.sajisehat.feature.trek.TrekViewModelFactory
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
import com.example.sajisehat.feature.trek.TrekDetailViewModel
import com.example.sajisehat.feature.trek.TrekDetailViewModelFactory
import com.example.sajisehat.feature.trek.TrekManualViewModel
import com.example.sajisehat.feature.trek.TrekManualViewModelFactory
import com.example.sajisehat.feature.trek.save.SaveToTrekScreen
import com.example.sajisehat.feature.trek.save.SaveTrekSuccessScreen
import com.example.sajisehat.feature.trek.ui.ManualCalcScreen
import com.example.sajisehat.feature.trek.ui.ManualInputScreen
import com.example.sajisehat.feature.trek.ui.TrekDetailScreen
import com.example.sajisehat.feature.trek.ui.TrekScreen
import com.example.sajisehat.navigation.Dest
import com.example.sajisehat.navigation.isOnRoute
import com.example.sajisehat.navigation.navigateSingleTopTo
import com.example.sajisehat.ui.components.bottombar.AppBottomBar
import com.example.sajisehat.ui.components.bottombar.BottomNavItem
import com.example.sajisehat.ui.components.bottombar.CenterScanFab
import com.example.sajisehat.ui.components.bottombar.rememberBottomBarSpec
import java.time.LocalDate

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
            composable(Dest.Trek.route) { backStackEntry ->
                // Buat ViewModel untuk Trek
                val trekViewModel: TrekViewModel = viewModel(
                    factory = TrekViewModelFactory(
                        trekRepository = AppGraph.trekRepository,
                        authRepository = AppGraph.authRepo
                    )
                )

                // Observasi uiState dari ViewModel
                val state by trekViewModel.uiState.collectAsState()

                TrekScreen(
                    state = state,
                    onPrevMonth = trekViewModel::onPrevMonth,
                    onNextMonth = trekViewModel::onNextMonth,
                    onSeeDetailToday = {
                        val todayString = LocalDate.now().toString() // "yyyy-MM-dd"
                        nav.navigate(Dest.TrekDetail.route(todayString))
                    }
                )
            }

            composable(Dest.Scan.route) {
                ScanRoute(navController = nav)
            }
            composable(Dest.Catalog.route) { CatalogScreen() }
            composable(Dest.Profile.route) { ProfileScreen() }
            composable(Dest.Detail.route)  { back ->
                val id = back.arguments?.getString("id").orEmpty()
                DetailScreen(id)
            }
            composable(
                route = Dest.SaveTrek.route,
                arguments = listOf(
                    navArgument("sugar") { type = NavType.FloatType }
                )
            ) { backStackEntry ->
                val sugar = backStackEntry.arguments?.getFloat("sugar")?.toDouble() ?: 0.0

                SaveToTrekScreen(
                    sugarGram = sugar,
                    onBack = { nav.popBackStack() },
                    onSaved = {
                        nav.navigate(Dest.SaveTrekSuccess.route) {
                            // hapus halaman form dari backstack, biar kalau back
                            // nggak balik ke form yang sama
                            popUpTo(Dest.SaveTrek.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Dest.SaveTrekSuccess.route) {
                SaveTrekSuccessScreen(
                    onGoToTrek = {
                        nav.navigate(Dest.Trek.route) {
                            popUpTo(Dest.Home.route) { inclusive = false }
                        }
                    }
                )
            }
            composable(
                route = Dest.TrekDetail.route,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date").orEmpty()

                val trekDetailViewModel: TrekDetailViewModel = viewModel(
                    factory = TrekDetailViewModelFactory(
                        trekRepository = AppGraph.trekRepository,
                        authRepository = AppGraph.authRepo,
                        dateString = dateString
                    )
                )

                val detailState by trekDetailViewModel.uiState.collectAsState()

                TrekDetailScreen(
                    state = detailState,
                    onBack = { nav.popBackStack() },
                    onDeleteItem = { id -> trekDetailViewModel.onDeleteItem(id) },
                    onAddManual = {
                        nav.navigate(Dest.TrekManualInput.route(dateString))
                    }
                )

            }
            composable(
                route = Dest.TrekManualInput.route,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date").orEmpty()

                val manualViewModel: TrekManualViewModel = viewModel(
                    factory = TrekManualViewModelFactory(
                        trekRepository = AppGraph.trekRepository,
                        authRepository = AppGraph.authRepo,
                        dateString = dateString
                    )
                )

                val inputState by manualViewModel.inputState.collectAsState()

                ManualInputScreen(
                    state = inputState,
                    onBack = { nav.popBackStack() },
                    onProductNameChange = manualViewModel::onProductNameChange,
                    onSugarPerServingChange = manualViewModel::onSugarPerServingChange,
                    onServingSizeChange = manualViewModel::onServingSizeChange,
                    onNext = {
                        val result = manualViewModel.buildManualResult()
                        if (result != null) {
                            nav.navigate(Dest.TrekManualCalc.route(dateString))
                        }
                    }
                )
            }
            composable(
                route = Dest.TrekManualCalc.route,
                arguments = listOf(
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val dateString = backStackEntry.arguments?.getString("date").orEmpty()

                // Ambil entry SEBELUM route ini (yaitu TrekManualInput)
                val parentEntry = remember(backStackEntry) {
                    nav.previousBackStackEntry ?: backStackEntry
                }

                // Pakai factory yang sama seperti waktu bikin di ManualInput
                val factory = TrekManualViewModelFactory(
                    trekRepository = AppGraph.trekRepository,
                    authRepository = AppGraph.authRepo,
                    dateString = dateString
                )

                val manualViewModel: TrekManualViewModel =
                    ViewModelProvider(parentEntry, factory)[TrekManualViewModel::class.java]

                val result = manualViewModel.getLastResult()
                    ?: manualViewModel.buildManualResult()
                    ?: run {
                        nav.popBackStack()
                        return@composable
                    }

                ManualCalcScreen(
                    result = result,
                    onBack = { nav.popBackStack() },
                    onAddToTrek = {
                        manualViewModel.saveToTrek(
                            onSuccess = {
                                nav.navigate(Dest.SaveTrekSuccess.route) {
                                    popUpTo(Dest.Trek.route) { inclusive = false }
                                }
                            },
                            onError = {
                                println("Error save manual trek: $it")
                            }
                        )
                    }
                )
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
