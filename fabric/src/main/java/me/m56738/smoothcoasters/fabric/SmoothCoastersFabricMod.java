package me.m56738.smoothcoasters.fabric;

import me.m56738.smoothcoasters.common.SmoothCoasters;
import me.m56738.smoothcoasters.common.SmoothCoastersDebugHudEntry;
import me.m56738.smoothcoasters.fabric.network.FabricCustomPayloadRegistrar;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.resources.Identifier;

public class SmoothCoastersFabricMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        String version = FabricLoader.getInstance().getModContainer("smoothcoasters")
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse("unknown");

        SmoothCoasters sc = new SmoothCoasters(version);
        SmoothCoasters.setInstance(sc);

        sc.registerCustomPayloadTypes(new FabricCustomPayloadRegistrar());
        sc.registerKeyMappings(new FabricKeyMappingRegistrar());

        ClientTickEvents.END_CLIENT_TICK.register(_ -> sc.tick());

        DebugScreenEntries.register(Identifier.fromNamespaceAndPath("smoothcoasters", "smoothcoasters_version"), new SmoothCoastersDebugHudEntry());
    }
}
