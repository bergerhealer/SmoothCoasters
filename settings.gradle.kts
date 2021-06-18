rootProject.name = "SmoothCoasters"

pluginManagement {
    repositories {
        jcenter()
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }

    plugins {
        id("fabric-loom") version "0.7-SNAPSHOT"
    }
}
