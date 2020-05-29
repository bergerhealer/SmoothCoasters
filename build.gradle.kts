plugins {
    java
    id("fabric-loom")
    id("maven-publish")
}

group = "me.m56738"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.fabricmc.net/")
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:1.15.2")
    mappings("net.fabricmc:yarn:1.15.2+build.15:v2")
    modImplementation("net.fabricmc:fabric-loader:0.8.2+build.194")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.10.1+build.307-1.15")
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
