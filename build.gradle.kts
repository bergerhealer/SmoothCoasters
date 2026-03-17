plugins {
    java
    id("fabric-loom") version "1.4-SNAPSHOT"
    id("maven-publish")
}

group = "me.m56738"
version = "1.18.2-v2"

dependencies {
    minecraft("com.mojang:minecraft:1.18.2")
    mappings("net.fabricmc:yarn:1.18.2+build.3:v2")
    modImplementation("net.fabricmc:fabric-loader:0.14.6")

    val fabricApiVersion = "0.53.4+1.18.2"
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", fabricApiVersion))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))
    modImplementation(fabricApi.module("fabric-networking-api-v1", fabricApiVersion))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}
