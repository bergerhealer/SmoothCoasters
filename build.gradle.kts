plugins {
    java
    id("fabric-loom") version "0.12-SNAPSHOT"
    id("maven-publish")
}

group = "me.m56738"
version = "1.19-v1"

dependencies {
    minecraft("com.mojang:minecraft:1.19")
    mappings("net.fabricmc:yarn:1.19+build.1:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.6")
    modImplementation(fabricApi.module("fabric-networking-api-v1", "0.55.2+1.19"))
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
