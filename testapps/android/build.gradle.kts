plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "co.touchlab.kampkit.android"
    compileSdk = libs.versions.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "co.touchlab.kampkit"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }

    lint {
        warningsAsErrors = false
        abortOnError = true
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

val remoteBuild = false

if (remoteBuild) {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/[your org]/[your repo]")
            credentials {
                username = project.property("GITHUB_PACKAGES_USERNAME") as String
                password = project.property("GITHUB_PACKAGES_PASSWORD") as String
            }
        }
    }
}

val GROUP:String by project
val LIBRARY_VERSION:String by project

dependencies {
    if (remoteBuild) {
        implementation("${GROUP}:analytics-android-debug:${LIBRARY_VERSION}.+")
        implementation("${GROUP}:breeds-android-debug:${LIBRARY_VERSION}.+")
    } else {
        implementation(project(":analytics"))
        implementation(project(":breeds"))
    }
    implementation(libs.bundles.app.ui)
    implementation(libs.koin.android)
    coreLibraryDesugaring(libs.android.desugaring)
}
