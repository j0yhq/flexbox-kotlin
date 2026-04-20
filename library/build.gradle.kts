import com.android.build.api.dsl.androidLibrary
import java.net.URI
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
}

group = "io.joy.flex"
version = "0.1.0"

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()
    androidLibrary {
        namespace = "io.joy.flex"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()
        withHostTestBuilder {}.configure {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions.jvmTarget = JvmTarget.JVM_17
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.compose.test)
        }

        val androidHostTest by getting {
            dependencies {
                implementation(libs.compose.test)
                implementation(libs.robolectric)
            }
        }

        val iosTest by getting {
            dependencies {
                implementation(libs.compose.test)
            }
        }
    }
}

dokka {
    moduleName.set("Flex")
    dokkaSourceSets {
        configureEach {
            includes.from("Module.md")
            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl.set(URI("https://github.com/j0yhq/flexbox-kotlin/tree/main/library/src"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "flex", version.toString())

    pom {
        name = "Flex"
        description = "A Kotlin Multiplatform library providing a flexbox layout model for Compose."
        inceptionYear = "2026"
        url = "https://github.com/j0yhq/flexbox-kotlin/"
        licenses {
            license {
                name = "MIT"
                url = "https://mit-license.org/"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "iamlooker"
                name = "LooKeR"
                url = "https://looker.sh"
                properties = mapOf(
                    "Codeberg" to "https://codeberg.org/Iamlooker",
                    "GitHub" to "https://github.com/Iamlooker",
                )
            }
        }
        scm {
            url = "https://github.com/j0yhq/flexbox-kotlin/"
            connection = "scm:git:git://github.com/j0yhq/flexbox-kotlin.git"
            developerConnection = "scm:git:ssh://git@github.com/j0yhq/flexbox-kotlin.git"
        }
    }
}
