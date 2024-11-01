plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.fabric.loom)
}

group = "me.m56738"
version = "1.21.3-v1-SNAPSHOT"

dependencies {
    minecraft(libs.minecraft)
    mappings(variantOf(libs.yarn) { classifier("v2") })
    modImplementation(libs.fabric.loader)
    modImplementation(fabricApi.module("fabric-key-binding-api-v1", libs.versions.fabric.api.get()))
    modImplementation(fabricApi.module("fabric-lifecycle-events-v1", libs.versions.fabric.api.get()))
    modImplementation(fabricApi.module("fabric-networking-api-v1", libs.versions.fabric.api.get()))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand("version" to project.version)
        }
    }
}
