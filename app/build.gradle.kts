plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.ksp)
    kotlin("plugin.serialization") version libs.versions.kotlin.get()
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "dev.bltucker.spendless"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.bltucker.spendless"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    testOptions{
        unitTests.isReturnDefaultValues = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    arguments {
        arg("dagger.fastInit", "true")
        arg("dagger.hilt.android.internal.disableAndroidSuperclassValidation", "true")
        arg("kapt.kotlin.generated", "true")
    }
}

dependencies {

    // Core
    implementation(libs.androidx.core.ktx)
    coreLibraryDesugaring(libs.android.desugar)

    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.activity)
    implementation(libs.compose.animation)
    implementation(libs.compose.material3)
    implementation(libs.compose.icons.extended)
    implementation(libs.compose.tooling.preview)
    debugImplementation(libs.compose.tooling)

    // Kotlinx
    implementation(libs.kotlinx.serialization.json)

    // Lifecycle
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.savedstate)
    implementation(libs.lifecycle.service)
    implementation(libs.lifecycle.process)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.hilt.navigation.compose)

    // Dependency Injection
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Image Loading
    implementation(libs.coil.compose)

    // Local Storage
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.datastore)

    //Splash
    implementation(libs.core.splashscreen)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.arch.core.testing)
    testImplementation(libs.lifecycle.runtime.testing)
    testImplementation(libs.mockk)
    testImplementation(libs.room.testing)


    androidTestImplementation(libs.junit.ext)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.arch.core.testing)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.room.testing)
    androidTestImplementation(libs.coroutines.test)
}