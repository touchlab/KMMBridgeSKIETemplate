@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    id("co.touchlab.kmmbridge")
    id("co.touchlab.skie")
    kotlin("native.cocoapods")
    `maven-publish`
}

kotlin {
    @Suppress("OPT_IN_USAGE")
    targetHierarchy.default()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "KMMBridgeSKIETemplate"
        homepage = "https://www.touchlab.co"
        ios.deploymentTarget = "13.5"
        extraSpecAttributes["libraries"] = "'c++', 'sqlite3'"
        license = "BSD"
        extraSpecAttributes.put("swift_version", "\"5.0\"") // <- SKIE Needs this!
        framework {
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
    }
}

addGithubPackagesRepository()

kmmbridge {
    mavenPublishArtifacts()
    spm()
    cocoapods("git@github.com:touchlab/KotlinPodspecs.git")
}
