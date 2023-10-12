pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

include("breeds", "analytics", "allshared", ":testapps:android")
