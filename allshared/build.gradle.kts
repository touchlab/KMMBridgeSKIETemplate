import co.touchlab.faktory.versionmanager.GitRemoteVersionWriter
import org.jetbrains.kotlin.gradle.plugin.mpp.Framework

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    id("co.touchlab.faktory.kmmbridge")
    id("co.touchlab.skie")
    `maven-publish`
}

kotlin {
    targetHierarchy.default()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            export(project(":analytics"))
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":breeds"))
                api(project(":analytics"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.bundles.shared.commonTest)
            }
        }
    }
}

addGithubPackagesRepository()

kmmbridge {
    mavenPublishArtifacts()
    manualVersions()
    noGitOperations()
    spm()

//    cocoapods("git@github.com:touchlab/PodSpecs.git")
}
