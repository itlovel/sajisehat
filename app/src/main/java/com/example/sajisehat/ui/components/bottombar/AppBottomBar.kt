package com.example.sajisehat.ui.components.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sajisehat.navigation.isOnRoute
import com.example.sajisehat.navigation.navigateSingleTopTo
import com.example.sajisehat.ui.theme.SajiTextStyles

@Composable
fun AppBottomBar(
    nav: NavHostController,
    leftItems: List<BottomNavItem>,
    rightItems: List<BottomNavItem>,
    barHeight: Dp,
    corner: Dp,
    haloSize: Dp,
    iconSize: Dp,
    spacerWidth: Dp,
    showLabels: Boolean,
    centerLabel: String,
    centerSelected: Boolean,
    onCenterClick: () -> Unit
) {
    val backEntry by nav.currentBackStackEntryAsState()
    val dest = backEntry?.destination

    // full-bleed: tanpa padding kiri/kanan
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = -(haloSize / 2))
                .size(haloSize)
                .zIndex(1f)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )

        val barShape = RoundedCornerShape(
            topStart = corner, topEnd = corner,
            bottomStart = 0.dp, bottomEnd = 0.dp
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(barHeight)
                .fillMaxWidth(),
            shape = barShape,
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 4.dp,
            shadowElevation = 6.dp
        ) {
            NavigationBar(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                // kiri
                leftItems.forEach { item ->
                    val selected = dest.isOnRoute(item.route)
                    val resId = if (selected) item.iconSelectedRes else item.iconRes
                    NavigationBarItem(
                        selected = selected,
                        onClick = { nav.navigateSingleTopTo(item.route) },
                        icon = {
                            Icon(
                                painter = painterResource(resId),
                                contentDescription = item.label,
                                modifier = Modifier.size(iconSize),
                                tint = if (item.useTint)
                                    (if (selected) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.onPrimary)
                                else Color.Unspecified
                            )
                        },
                        label = { if (showLabels) Text(item.label, style = SajiTextStyles.Caption) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }

                // tengah: slot label "Scan"
                Box(
                    modifier = Modifier
                        .width(spacerWidth)
                        .fillMaxHeight()
                        .clickable { onCenterClick() },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (showLabels) {
                        Text(
                            centerLabel,
                            style = SajiTextStyles.Caption,
                            color = if (centerSelected)
                                MaterialTheme.colorScheme.secondary
                            else
                                MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

                // kanan
                rightItems.forEach { item ->
                    val selected = dest.isOnRoute(item.route)
                    val resId = if (selected) item.iconSelectedRes else item.iconRes
                    NavigationBarItem(
                        selected = selected,
                        onClick = { nav.navigateSingleTopTo(item.route) },
                        icon = {
                            Icon(
                                painter = painterResource(resId),
                                contentDescription = item.label,
                                modifier = Modifier.size(iconSize),
                                tint = if (item.useTint)
                                    (if (selected) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.onPrimary)
                                else Color.Unspecified
                            )
                        },
                        label = { if (showLabels) Text(item.label, style = SajiTextStyles.Caption) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.secondary,
                            selectedTextColor = MaterialTheme.colorScheme.secondary,
                            unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            unselectedTextColor = MaterialTheme.colorScheme.onPrimary,
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}
