import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose.hot.reload)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.ksp)
    alias(libs.plugins.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
        freeCompilerArgs.add("-opt-in=kotlin.uuid.ExperimentalUuidApi")
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            
            // Android-specific Koin
            implementation(libs.koin.android)

            // Splashscreen
            implementation(libs.androidx.splashscreen)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            
            // Lifecycle
            implementation(libs.bundles.androidx.lifecycle)
            
            // Dependency Injection
            implementation(libs.bundles.koin.common)
            
            // Serialization
            implementation(libs.kotlinx.serialization.json)
            
            // Image Loading
            implementation(libs.coil.compose)

            // Local Persistence
            implementation(libs.bundles.androidx.room)

            // Navigation
            implementation(libs.jetbrains.compose.navigation)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.plcoding.cmpmastermeme"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.plcoding.cmpmastermeme"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

dependencies {
    add("ksp", libs.androidx.room.compiler)
    debugImplementation(compose.uiTooling)
}

/*
    Fix KSP dependency on Compose resource generation (Android-only issue that looks to be an issue
    introduced in current dependency versions of either Room, KSP, or Kotlin
 */
afterEvaluate {
    tasks.matching { it.name.startsWith("ksp") && it.name.contains("Android") }.configureEach {
        dependsOn(tasks.matching { it.name.contains("generateResourceAccessors") })
        dependsOn(tasks.matching { it.name.contains("generateComposeResClass") })
        dependsOn(tasks.matching { it.name.contains("generateActualResourceCollectors") })
        dependsOn(tasks.matching { it.name.contains("generateExpectResourceCollectors") })
    }
}

compose.desktop {
    application {
        mainClass = "com.plcoding.cmpmastermeme.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.plcoding.cmpmastermeme"
            packageVersion = "1.0.0"
        }
    }
}
