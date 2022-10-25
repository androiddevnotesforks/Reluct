plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

android {
    namespace = "work.racka.reluct.common.features.settings"
}

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:model"))
                implementation(project(":common:domain"))
                implementation(project(":common:mvvm-core"))
                implementation(project(":common:persistence:settings"))

                // Features imported
                implementation(project(":common:features:screen-time"))

                implementation(libs.coroutines.core)
                implementation(libs.koin.core)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.koin.test)
                implementation(libs.coroutines.test)
                implementation(libs.turbine.test)
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting

        val androidTest by getting

        val desktopMain by getting

        val desktopTest by getting
    }
}
