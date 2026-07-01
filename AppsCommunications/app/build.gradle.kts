plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.appscommunications"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.appscommunications"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

androidComponents {
    onVariants { variant ->
        val kspAssets = layout.buildDirectory
            .dir("generated/ksp/${variant.name}/resources/assets")
        kspAssets.get().asFile.mkdirs()
        variant.sources.assets?.addStaticSourceDirectory(kspAssets.get().asFile.absolutePath)
    }
}

tasks.matching { it.name.startsWith("merge") && it.name.endsWith("Assets") }.configureEach {
    val capital = name.removePrefix("merge").removeSuffix("Assets")
    dependsOn(tasks.matching { it.name == "ksp${capital}Kotlin" })
}

ksp {
    arg("appfunctions:aggregateAppFunctions", "true")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(libs.androidx.appfunctions)
    implementation(libs.androidx.appfunctions.service)
    ksp(libs.androidx.appfunctions.compiler)

    // Firebase AI Logic (cloud Gemini + function calling)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    // JSON types used by firebase-ai's function-call args / responses.
    implementation(libs.kotlinx.serialization.json)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}