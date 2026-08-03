import net.darkhax.curseforgegradle.TaskPublishCurseForge

plugins {
    id("net.neoforged.moddev")
    alias(libs.plugins.shadow)
    alias(libs.plugins.resource.factory.neoforge.convention)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
}

val implementationShaded = configurations.create("implementationShaded")

configurations {
    implementation {
        extendsFrom(implementationShaded)
    }
}

neoForge {
    version = libs.versions.neoforge.get()

    mods {
        register("smoothcoasters") {
            sourceSet(sourceSets.main.get())
            sourceSet(project(":common").sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            client()
            ideName = "NeoForge"
        }
    }
}

dependencies {
    implementationShaded(project(":common"))
}

neoForgeModsToml {
    license = "MIT"
    mod("smoothcoasters") {
        version = project.version.toString()
        displayName = "SmoothCoasters"
        displayUrl = "https://modrinth.com/mod/smoothcoasters"
        authors = "56738"
        description = "Minecraft mod to enhance vanilla roller coasters"
        mixin("smoothcoasters.mixin.json")
    }
}

tasks {
    shadowJar {
        archiveBaseName = "SmoothCoasters-NeoForge"
        archiveClassifier = ""
        configurations.add(implementationShaded)
    }
}

modrinth {
    projectId = "smoothcoasters"
    uploadFile.set(tasks.shadowJar)
    changelog = provider { rootProject.file("CHANGELOG.md").readText() }
    syncBodyFrom = provider { rootProject.file("README.md").readText() }
}

tasks {
    register("publishCurseForge", TaskPublishCurseForge::class) {
        apiToken = System.getenv("CURSEFORGE_TOKEN")
        val mainFile = upload(397480, shadowJar)
        mainFile.displayName = version.toString()
        mainFile.changelog = rootProject.file("CHANGELOG.md").readText()
        mainFile.changelogType = "markdown"
        mainFile.releaseType = "release"
        mainFile.addGameVersion(libs.versions.minecraft.get())
        mainFile.addEnvironment("Client")
    }
}

