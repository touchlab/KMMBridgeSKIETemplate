plugins {
    kotlin("multiplatform")
    id("com.android.library")
    `maven-publish`
}

kotlin {
    @Suppress("OPT_IN_USAGE")
    targetHierarchy.default()
    androidTarget {
        publishAllLibraryVariants()
    }
    ios()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.touchlab.stately.concurrency)
            }
        }
    }
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    defaultConfig {
        @Suppress("UnstableApiUsage")
        minSdk = libs.versions.minSdk.get().toInt()
    }
    namespace = "co.touchlab.kmmbridgekickstart.analytics"
}

addGithubPackagesRepository()