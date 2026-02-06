import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.baselineprofile)
}

val enableVersionAudit = providers.gradleProperty("cebolao.enableVersionAudit").orNull == "true"

ktlint {
    // Formatting is enforced - codebase follows ktlint standards
    android.set(true)
    ignoreFailures.set(false)
}

android {
    namespace = "com.cebolao"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.cebolao"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    androidResources {
        localeFilters += listOf("pt", "pt-rBR")
    }

    composeCompiler {
        // Enable compose compiler metrics and reports for performance analysis
        // To generate: ./gradlew assembleDebug -Pcebolao.enableComposeReports=true
        if (project.findProperty("cebolao.enableComposeReports") == "true") {
            metricsDestination = layout.buildDirectory.dir("compose_metrics")
            reportsDestination = layout.buildDirectory.dir("compose_reports")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    lint {
        checkReleaseBuilds = true
        abortOnError = true
        // Keep normal lint focused on actionable app issues; run with
        // -Pcebolao.enableVersionAudit=true when explicitly auditing dependency updates.
        checkDependencies = enableVersionAudit
        if (!enableVersionAudit) {
            disable += "NewerVersionAvailable"
            disable += "GradleDependency"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
    }
}

dependencies {
    // Compose BOM
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Compose
    implementation(libs.compose.foundation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material3.window.size)
    implementation(libs.compose.material.icons.extended)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    // AndroidX
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.profileinstaller)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Retrofit & OkHttp
    implementation(libs.retrofit)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.retrofit2.kotlinx.serialization.converter)

    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // DataStore Preferences (user presets persistence)
    implementation(libs.androidx.datastore.preferences)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Testing
    testImplementation(libs.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.compose.ui.test.junit4)

    // Material Components (needed for Manifest theme)
    implementation(libs.material)

    // Desugaring
    coreLibraryDesugaring(libs.desugar.jdk.libs)

    // Baseline Profiles (startup / runtime performance)
    baselineProfile(project(":baselineprofile"))
}

ksp {
    arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
    arg("room.schemaLocation", "$projectDir/schemas")
}

baselineProfile {
    // Avoid generating profiles on every build; run explicitly when needed.
    automaticGenerationDuringBuild = false
}
