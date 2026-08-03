plugins {
    id("net.neoforged.moddev")
}

neoForge {
    neoFormVersion = libs.versions.neoform.get()
}

dependencies {
    compileOnly(libs.sponge.mixin)
}
