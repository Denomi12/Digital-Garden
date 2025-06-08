import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    kotlin("plugin.serialization") version "1.9.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.foundation)
    implementation(compose.material)


    implementation("com.squareup.okhttp3:okhttp:4.12.0") // za http zahtevke
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0") // za parsanje
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0") // za asinhronost
    implementation("ch.qos.logback:logback-classic:1.4.14") // logger knjižnica
    implementation(compose.materialIconsExtended)


    implementation("it.skrape:skrapeit:1.2.2") // Skrape knjižnica za scraping

}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "KotlinApp"
            packageVersion = "1.0.0"
        }
    }
}
