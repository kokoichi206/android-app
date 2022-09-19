plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    compileSdk = 32
    defaultConfig {
        applicationId = "jp.mydns.kokoichi0206.kmmtest.android"
        minSdk = 26
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    buildFeatures {
        // Enables Jetpack Compose for this module
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
}

dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.0")
    // Swipe to refresh
    implementation("com.google.accompanist:accompanist-swiperefresh:0.24.13-rc")

    // Integration with activities
    implementation("androidx.activity:activity-compose:1.5.1")
    // Compose Material Design
    implementation("androidx.compose.material:material:1.2.1")
    // Animations
    implementation("androidx.compose.animation:animation:1.2.1")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:1.2.1")
    // Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    // UI Tests
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.2.1")
}