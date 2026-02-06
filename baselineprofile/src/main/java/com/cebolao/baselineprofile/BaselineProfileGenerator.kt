package com.cebolao.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Baseline Profile generation test for startup optimization.
 *
 * This test generates a baseline profile that the Android Runtime uses
 * to ahead-of-time compile critical code paths, improving app startup time.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateBaselineProfile() = baselineProfileRule.collect(
        packageName = "com.cebolao",
        maxIterations = 15,
        stableIterations = 3,
        includeInStartupProfile = true
    ) {
        // Critical user journey: App startup
        pressHome()
        startActivityAndWait()

        // Wait for content to load
        device.waitForIdle()

        // Navigate through main features
        device.findObject(
            androidx.test.uiautomator.By.desc("Gerador")
        )?.click()
        device.waitForIdle()

        device.findObject(
            androidx.test.uiautomator.By.desc("Jogos")
        )?.click()
        device.waitForIdle()

        device.findObject(
            androidx.test.uiautomator.By.desc("Conferidor")
        )?.click()
        device.waitForIdle()

        device.findObject(
            androidx.test.uiautomator.By.desc("Início")
        )?.click()
        device.waitForIdle()
    }
}
