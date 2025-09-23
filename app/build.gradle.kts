import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties
import java.io.FileInputStream

val keyPropertiesFile = rootProject.file("key.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keyPropertiesFile))

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtool.ksp)
}

android {
    namespace = "com.deiovannagroup.books_catalog"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.deiovannagroup.books_catalog"
        minSdk = 26
        targetSdk = 36
        versionCode = 8
        versionName = "8.3.0 How Many Rooms"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties["keyAlias"] as String
            keyPassword = keystoreProperties["keyPassword"] as String
            storeFile = file(keystoreProperties["storeFile"] as String)
            storePassword = keystoreProperties["storePassword"] as String
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            isDebuggable = true

            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    buildToolsVersion = "36.0.0"
    ndkVersion = "29.0.14033849 rc4"
}

dependencies {
    // Room dependencies
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // KSP hilt dependency
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)

    // Kotlin Core dependencies
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.core.ktx)

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.androidx.room.testing)

    // View Model dependency
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // View components dependencies
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.cardview)
    implementation(libs.material)
}

// Custom task to print the version name
tasks.register("printVersionNumber") {
    doLast {
        val versionName = android.defaultConfig.versionName ?: ""
        val versionNumber = versionName.split(" ")[0]
        println(versionNumber)
    }
}