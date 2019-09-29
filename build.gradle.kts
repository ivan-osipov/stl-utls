import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.50"
}

group = "ru.i-osipov"
version = "1.0.0-SNAPSHOT"

repositories {
    jcenter()
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

object Versions {
    const val spek = "2.0.6"
    const val junit = "5.3.1"
    const val mockito = "2.2.0"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")

    testImplementation("org.spekframework.spek2:spek-dsl-jvm:${Versions.spek}")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:${Versions.mockito}")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:${Versions.spek}")
    testRuntimeOnly(kotlin("reflect"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Wrapper> {
    gradleVersion = "5.6"
}