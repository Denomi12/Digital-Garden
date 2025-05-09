plugins {
    kotlin("jvm") version "2.1.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("it.skrape:skrapeit:1.2.2") // Skrape knjižnica za scraping
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}
