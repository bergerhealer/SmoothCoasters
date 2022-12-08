plugins {
    java
    id("fabric-loom") version "1.0-SNAPSHOT"
    id("maven-publish")
}

group = "me.m56738"
version = "1.19.3-v1-SNAPSHOT"

dependencies {
    minecraft("com.mojang:minecraft:1.19.3")
    mappings("net.fabricmc:yarn:1.19.3+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.11")
    modImplementation(fabricApi.module("fabric-networking-api-v1", "0.68.1+1.19.3"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}
