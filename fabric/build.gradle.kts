import net.darkhax.curseforgegradle.TaskPublishCurseForge
import xyz.jpenilla.resourcefactory.fabric.Environment

plugins {
    id("net.fabricmc.fabric-loom")
    alias(libs.plugins.shadow)
    alias(libs.plugins.resource.factory.fabric.convention)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
}

val implementationIncluded = configurations.create("implementationIncluded")
val implementationShaded = configurations.create("implementationShaded")

configurations {
    implementation {
        extendsFrom(implementationIncluded)
        extendsFrom(implementationShaded)
    }
    include {
        extendsFrom(implementationIncluded)
    }
}

dependencies {
    minecraft(libs.minecraft)
    implementation(libs.fabric.loader)

    implementationShaded(project(":common"))

    implementationIncluded(fabricApiLibs.key.mapping.api.v1)
    implementationIncluded(fabricApiLibs.lifecycle.events.v1)
    implementationIncluded(fabricApiLibs.networking.api.v1)
    implementationIncluded(fabricApiLibs.resource.loader.v1)
    implementationIncluded(fabricApiLibs.transitive.access.wideners.v1)
    implementationIncluded(fabricApiLibs.base)
}

loom {
    mods {
        register("smoothcoasters") {
            sourceSet(sourceSets.main.get())
        }
    }

    runConfigs {
        named("client") {
            displayName = "Fabric"
            generateRunConfig = true
            appendProjectPathToDisplayName = false
        }
    }
}

fabricModJson {
    id = "smoothcoasters"
    name = "SmoothCoasters"
    description = "Minecraft mod to enhance vanilla roller coasters"
    environment = Environment.CLIENT
    author("56738")
    license("MIT")
    clientEntrypoint("me.m56738.smoothcoasters.fabric.SmoothCoastersFabricMod")
    mixin("smoothcoasters.mixin.json")
    contact {
        homepage = "https://github.com/bergerhealer/SmoothCoasters/wiki"
        sources = "https://github.com/bergerhealer/SmoothCoasters"
        issues = "https://github.com/bergerhealer/SmoothCoasters/issues"
    }
    depends("minecraft", "~" + libs.versions.minecraft.get())
    depends("fabricloader", "*")
    depends("fabric-key-mapping-api-v1", "*")
    depends("fabric-lifecycle-events-v1", "*")
    depends("fabric-networking-api-v1", "*")
    depends("fabric-resource-loader-v1", "*")
    depends("fabric-transitive-access-wideners-v1", "*")
}

tasks {
    shadowJar {
        archiveBaseName = "SmoothCoasters-Fabric"
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
