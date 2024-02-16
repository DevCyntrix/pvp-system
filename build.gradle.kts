import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `maven-publish`
    id("java-library")
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
}

group = "dev.sgffa"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")

    maven {
        name = "sgffa"
        url = uri("https://repo.ssr-solutions.de/releases")
        credentials(PasswordCredentials::class)
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("dev.sgffa:api:1.0.1")
    compileOnly("org.jetbrains:annotations:24.1.0")

    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

paper {
    main = "dev.sgffa.pvp.PvPPlugin"
    apiVersion = "1.20"
    generateLibrariesJson = true

    serverDependencies {
        register("api") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    build {
        dependsOn("shadowJar")
    }
    runServer {
        minecraftVersion("1.20.2")
    }
}

publishing {
    repositories {
        maven {
            val releaseUri = uri("https://repo.ssr-solutions.de/releases")
            val snapshotUri = uri("https://repo.ssr-solutions.de/snapshots")

            val targetUri = if (version.toString().endsWith("SNAPSHOT")) snapshotUri else releaseUri

            name = "sgffa"
            url = targetUri
            credentials(PasswordCredentials::class)
        }
    }
    publications {
        create<MavenPublication>("sgffa") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()


            from(components["java"])
        }
    }
}