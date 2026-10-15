
plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "org.example"
version = "0.1.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.github.Gulzar-TechlogixIT:kot-lib-1:0.1.0")

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.example.AppKt")
}

tasks.test {
    useJUnitPlatform()
}

