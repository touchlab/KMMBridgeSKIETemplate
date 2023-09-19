pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = java.net.URI("https://oss.sonatype.org/content/repositories/snapshots")
        }
    }
}

include("breeds", "analytics", "allshared", ":testapps:android")
