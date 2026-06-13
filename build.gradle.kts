import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("java")
    id("maven-publish")
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
}

group = "me.m56738"
version = "26.2-v1-SNAPSHOT"

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)

    val fabricApiVersion = libs.versions.fabric.api.get()
    implementation(include(fabricApi.module("fabric-key-mapping-api-v1", fabricApiVersion))!!)
    implementation(include(fabricApi.module("fabric-lifecycle-events-v1", fabricApiVersion))!!)
    implementation(include(fabricApi.module("fabric-networking-api-v1", fabricApiVersion))!!)
    implementation(include(fabricApi.module("fabric-resource-loader-v1", fabricApiVersion))!!)
    implementation(include(fabricApi.module("fabric-transitive-access-wideners-v1", fabricApiVersion))!!)
    implementation(include(fabricApi.module("fabric-api-base", fabricApiVersion))!!)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
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
    uploadFile.set(tasks.jar)
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    syncBodyFrom = provider { rootProject.file("README.md").readText() }
}

tasks {
    register("publishCurseForge", TaskPublishCurseForge::class) {
        apiToken = System.getenv("CURSEFORGE_TOKEN")
        val mainFile = upload(397480, jar)
        mainFile.displayName = version.toString()
        mainFile.changelog = rootProject.file("CHANGELOG.md").readText()
        mainFile.changelogType = "markdown"
        mainFile.releaseType = "release"
        mainFile.addGameVersion(libs.versions.minecraft.get())
        mainFile.addEnvironment("Client")
    }
}
