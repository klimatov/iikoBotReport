import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.20"
    application
}

group = "com.github.klimatov"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("dev.inmo:tgbotapi:2.0.2")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("MainKt")
}

