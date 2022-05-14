plugins {
    java
    id("fabric-loom")
    id("maven-publish")
}

group = "me.m56738"
version = "1.18.2-v1-SNAPSHOT"

dependencies {
    minecraft("com.mojang:minecraft:1.18.2")
    mappings("net.fabricmc:yarn:1.18.2+build.3:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.5")
    modImplementation(fabricApi.module("fabric-networking-api-v1", "0.51.1+1.18.2"))
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
