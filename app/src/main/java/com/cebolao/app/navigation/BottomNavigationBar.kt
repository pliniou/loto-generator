package com.cebolao.app.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import com.cebolao.R

/**
 * Data class representing a bottom navigation item.
 */
data class BottomNavItem(
    val route: Route,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

private val bottomNavItems =
    listOf(
        BottomNavItem(Route.Home, R.string.nav_home, Icons.Filled.Home),
        BottomNavItem(Route.Generator, R.string.nav_generator, Icons.Filled.Casino),
        BottomNavItem(Route.Games, R.string.nav_games, Icons.Filled.Calculate),
        BottomNavItem(Route.Checker, R.string.nav_checker, Icons.Filled.CheckCircle),
        BottomNavItem(Route.About, R.string.nav_about, Icons.Filled.Info),
    )

/**
 * Bottom navigation bar with type-safe navigation support.
 */
@Composable
fun CebolaoBottomBar(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(modifier = modifier) {
        bottomNavItems.forEach { item ->
            // Check if this item's route matches current destination
            val isSelected =
                when (item.route) {
                    is Route.Home -> currentDestination?.hasRoute<Route.Home>() == true
                    is Route.Generator -> currentDestination?.hasRoute<Route.Generator>() == true
                    is Route.Games -> currentDestination?.hasRoute<Route.Games>() == true
                    is Route.Checker -> currentDestination?.hasRoute<Route.Checker>() == true
                    is Route.About -> currentDestination?.hasRoute<Route.About>() == true
                    else -> false
                }

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo<Route.Home> {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelRes),
                    )
                },
                label = {
                    Text(text = stringResource(item.labelRes))
                },
            )
        }
    }
}
