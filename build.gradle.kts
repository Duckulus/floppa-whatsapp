plugins {
    kotlin("jvm") version "1.8.20"
    kotlin("plugin.serialization") version "1.8.20"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "de.duckulus"
version = "0.0.1"



repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-Beta")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    val ktorVersion = "2.2.4"
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    val exposedVersion = "0.41.1"
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.xerial:sqlite-jdbc:3.41.2.1")
    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("com.aallam.openai:openai-client:3.2.0")

    implementation("com.github.auties00:whatsappweb4j:3.3.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("org.openpnp:opencv:4.5.1-2")

    implementation("ch.qos.logback:logback-classic:1.4.7")
    implementation("org.codehaus.janino:janino:3.1.9")
    implementation("io.github.oshai:kotlin-logging-jvm:4.0.0-beta-22")
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