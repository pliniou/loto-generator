package com.cebolao.app.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe Navigation Routes using Kotlin Serialization.
 *
 * This replaces the old string-based Route class with type-safe navigation
 * introduced in Navigation Compose 2.8.0+.
 */
sealed interface Route {
    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Generator : Route

    @Serializable
    data object Games : Route

    @Serializable
    data object Checker : Route

    @Serializable
    data object About : Route

    @Serializable
    data object Statistics : Route
}
