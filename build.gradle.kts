plugins {
    java
    id("fabric-loom")
    id("maven-publish")
}

group = "me.m56738"
version = "1.18.1-v1"

dependencies {
    minecraft("com.mojang:minecraft:1.18.1")
    mappings("net.fabricmc:yarn:1.18.1+build.22:v2")
    modImplementation("net.fabricmc:fabric-loader:0.13.1")
    modImplementation(fabricApi.module("fabric-networking-api-v1", "0.46.4+1.18"))
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
