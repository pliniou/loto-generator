package com.cebolao.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cebolao.R
import com.cebolao.app.component.CebolaoTopAppBar
import com.cebolao.app.navigation.CebolaoBottomBar
import com.cebolao.app.navigation.CebolaoNavHost
import com.cebolao.app.navigation.Route
import com.cebolao.app.theme.CebolaoTheme

/**
 * Composable raiz do app.
 * Configura tema, navegação e bottom bar.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CebolaoApp(startDestination: Route = Route.Home) {
    CebolaoTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        // Check if current destination is Onboarding using route class
        val isOnboarding = currentDestination?.hasRoute<Route.Onboarding>() == true

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (!isOnboarding) {
                    val titleRes =
                        when {
                            currentDestination?.hasRoute<Route.Home>() == true -> R.string.home_title
                            currentDestination?.hasRoute<Route.Generator>() == true -> R.string.generator_title
                            currentDestination?.hasRoute<Route.Games>() == true -> R.string.games_title
                            currentDestination?.hasRoute<Route.Checker>() == true -> R.string.checker_title
                            currentDestination?.hasRoute<Route.About>() == true -> R.string.about_title
                            else -> R.string.app_name
                        }
                    CebolaoTopAppBar(
                        title = stringResource(titleRes),
                    )
                }
            },
            bottomBar = {
                if (!isOnboarding) {
                    CebolaoBottomBar(navController = navController)
                }
            },
        ) { innerPadding ->
            CebolaoNavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(innerPadding),
            )
        }
    }
}
