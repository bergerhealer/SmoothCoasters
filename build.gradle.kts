plugins {
    java
    id("fabric-loom")
    id("maven-publish")
}

group = "me.m56738"
version = "1.18-v1-SNAPSHOT"

dependencies {
    minecraft("com.mojang:minecraft:1.18.1")
    mappings("net.fabricmc:yarn:1.18.1+build.12:v2")
    modImplementation("net.fabricmc:fabric-loader:0.12.12")
    modImplementation(fabricApi.module("fabric-networking-api-v1", "0.45.0+1.18"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(Pair("version", project.version))
        }
    }
}
