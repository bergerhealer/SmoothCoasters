import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
}

group = "me.m56738"
version = "1.21.10-v2-SNAPSHOT"

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)

    val fabricApiVersion = libs.versions.fabric.api.get()
    modImplementation(include(fabricApi.module("fabric-key-binding-api-v1", fabricApiVersion))!!)
    modImplementation(include(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))!!)
    modImplementation(include(fabricApi.module("fabric-networking-api-v1", fabricApiVersion))!!)
    modImplementation(include(fabricApi.module("fabric-transitive-access-wideners-v1", fabricApiVersion))!!)
    modImplementation(include(fabricApi.module("fabric-api-base", fabricApiVersion))!!)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks {
    processResources {
        val props = mapOf(
            "version" to project.version,
            "minecraftVersion" to libs.versions.minecraft.get()
        )
        inputs.properties(props)
        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }
}

modrinth {
    projectId = "smoothcoasters"
    uploadFile.set(tasks.remapJar)
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    syncBodyFrom = provider { rootProject.file("README.md").readText() }
}

tasks {
    register("publishCurseForge", TaskPublishCurseForge::class) {
        apiToken = System.getenv("CURSEFORGE_TOKEN")
        val mainFile = upload(397480, remapJar)
        mainFile.changelog = rootProject.file("CHANGELOG.md").readText()
        mainFile.changelogType = "markdown"
        mainFile.releaseType = "release"
        mainFile.addGameVersion(libs.versions.minecraft.get())
        mainFile.addEnvironment("Client")
    }
}
