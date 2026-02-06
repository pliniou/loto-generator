package com.cebolao.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.cebolao.app.feature.about.AboutScreen
import com.cebolao.app.feature.checker.CheckerScreen
import com.cebolao.app.feature.checker.CheckerPrefillArgs
import com.cebolao.app.feature.games.GamesScreen
import com.cebolao.app.feature.generator.GeneratorScreen
import com.cebolao.app.feature.home.HomeScreen

/**
 * Type-safe Navigation Host using Navigation Compose 2.8.0+ APIs.
 */
@Composable
fun CebolaoNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: Route = Route.Home,
    isLargeScreen: Boolean = false,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Route.Onboarding> {
            com.cebolao.app.feature.onboarding.OnboardScreen(
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }
        composable<Route.Home> {
            HomeScreen(
                isLargeScreen = isLargeScreen,
                onNavigateToChecker = {
                    navController.navigate(Route.Checker)
                },
            )
        }
        composable<Route.Generator> {
            GeneratorScreen(isLargeScreen = isLargeScreen)
        }
        composable<Route.Games> {
            GamesScreen(
                onNavigateToGenerator = { navController.navigate(Route.Generator) },
                onNavigateToChecker = { game ->
                    navController.navigate(
                        Route.CheckerPrefill(
                            type = game.lotteryType,
                            numbers = game.numbers,
                            teamNumber = game.teamNumber,
                        ),
                    )
                },
            )
        }
        composable<Route.Checker> {
            CheckerScreen()
        }
        composable<Route.CheckerPrefill> { backStackEntry ->
            val args = backStackEntry.toRoute<Route.CheckerPrefill>()
            CheckerScreen(
                prefillArgs =
                    CheckerPrefillArgs(
                        type = args.type,
                        numbers = args.numbers,
                        teamNumber = args.teamNumber,
                    ),
            )
        }
        composable<Route.About> {
            AboutScreen(isLargeScreen = isLargeScreen)
        }
        composable<Route.Statistics> {
            com.cebolao.app.feature.statistics.StatisticsScreen()
        }
    }
}
