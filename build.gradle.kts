plugins {
    java
    id("fabric-loom")
    id("maven-publish")
}

group = "me.m56738"
version = "1.16.4-${System.getenv("BUILD_NUMBER") ?: "SNAPSHOT"}"

repositories {
    maven("https://maven.fabricmc.net/")
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.16.4")
    mappings("net.fabricmc:yarn:1.16.4+build.7:v2")
    modImplementation("net.fabricmc:fabric-loader:0.10.8")
    modImplementation(fabricApi.module("fabric-networking-api-v1", "0.28.4+1.16"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    processResources {
        inputs.property("version", project.version)

        from(sourceSets.main.get().resources.srcDirs) {
            include("fabric.mod.json")
            expand(Pair("version", project.version))
        }

        from(sourceSets.main.get().resources.srcDirs) {
            exclude("fabric.mod.json")
        }
    }
}
