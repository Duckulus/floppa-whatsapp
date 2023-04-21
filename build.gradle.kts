plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.duckulus"
version = "0.0.1"

val ktorVersion = "2.2.4"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("com.aallam.openai:openai-client:3.2.0")

    implementation("com.github.auties00:whatsappweb4j:3.3.0")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
}

val main = "de.duckulus.floppa.ManagerKt"

application {
    mainClass.set(main)
    applicationDefaultJvmArgs = listOf("--enable-preview")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = main
    }
}

kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("--enable-preview")
}