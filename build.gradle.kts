@Suppress("DSL_SCOPE_VIOLATION") // https://github.com/gradle/gradle/issues/22797 - Fixed in Gradle 8.1
plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.fabric.loom)
}

group = "me.m56738"
version = "1.19.4-v2-SNAPSHOT"

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
        languageVersion.set(JavaLanguageVersion.of(17))
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
