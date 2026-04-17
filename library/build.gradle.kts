import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.net.URI

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.dokka)
}

group = "io.joy.flowcompose"
version = "0.1.0"

kotlin {
    applyDefaultHierarchyTemplate()
    jvm()
    androidLibrary {
        namespace = "io.joy.flowcompose"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()
        withHostTestBuilder {}.configure {
            isIncludeAndroidResources = true
        }
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
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
    moduleName.set("FlowCompose")
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

    coordinates(group.toString(), "flowcompose", version.toString())

    pom {
        name = "FlowCompose"
        description = "A Kotlin Multiplatform library providing a flexbox layout model for Compose."
        inceptionYear = "2026"
        url = "https://github.com/joyfill/flowcompose/"
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
