plugins {
    java
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("maven-publish")
}

group = "me.m56738"
version = "1.17.1-v3"

dependencies {
    minecraft("com.mojang:minecraft:1.17.1")
    mappings("net.fabricmc:yarn:1.17.1+build.65:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.6")
    modImplementation(fabricApi.module("fabric-networking-api-v1", "0.46.1+1.17"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}
