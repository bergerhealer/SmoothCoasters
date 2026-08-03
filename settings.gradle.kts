rootProject.name = "SmoothCoasters"

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}

plugins {
    id("net.fabricmc.fabric-loom-repositories") version "1.17-SNAPSHOT"
    id("net.neoforged.moddev.repositories") version "2.0.143"
}

dependencyResolutionManagement {
    versionCatalogs {
        create("fabricApiLibs") {
            from("net.fabricmc.fabric-api:fabric-api-catalog:0.152.1+26.2")
        }
    }
}

include("common")
include("fabric")
include("neoforge")
