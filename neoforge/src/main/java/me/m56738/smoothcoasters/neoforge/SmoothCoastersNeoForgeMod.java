package me.m56738.smoothcoasters.neoforge;

import me.m56738.smoothcoasters.common.SmoothCoasters;
import me.m56738.smoothcoasters.common.SmoothCoastersDebugHudEntry;
import me.m56738.smoothcoasters.neoforge.network.NeoForgeCustomPayloadRegistrar;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterDebugEntriesEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(SmoothCoasters.MOD_ID)
public class SmoothCoastersNeoForgeMod {
    public SmoothCoastersNeoForgeMod(ModContainer modContainer, IEventBus modBus) {
        SmoothCoasters.setInstance(new SmoothCoasters(modContainer.getModInfo().getVersion().toString()));
        modBus.addListener(RegisterPayloadHandlersEvent.class, this::onRegisterPayloadHandlers);
        modBus.addListener(RegisterKeyMappingsEvent.class, this::onRegisterKeyMappings);
        modBus.addListener(RegisterDebugEntriesEvent.class, this::onRegisterDebugEntries);

        NeoForge.EVENT_BUS.addListener(ClientTickEvent.Post.class, this::onClientTickPost);
    }

    private void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
        SmoothCoasters.getInstance().registerCustomPayloadTypes(new NeoForgeCustomPayloadRegistrar(event.registrar("1").optional()));
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        SmoothCoasters.getInstance().registerKeyMappings(new NeoForgeKeyMappingRegistrar(event));
    }

    private void onClientTickPost(ClientTickEvent.Post event) {
        SmoothCoasters.getInstance().tick();
    }

    private void onRegisterDebugEntries(RegisterDebugEntriesEvent event) {
        event.register(Identifier.fromNamespaceAndPath("smoothcoasters", "smoothcoasters_version"), new SmoothCoastersDebugHudEntry());
    }
}
