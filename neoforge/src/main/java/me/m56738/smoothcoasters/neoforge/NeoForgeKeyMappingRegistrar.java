package me.m56738.smoothcoasters.neoforge;

import me.m56738.smoothcoasters.common.KeyMappingRegistrar;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public class NeoForgeKeyMappingRegistrar implements KeyMappingRegistrar {
    private final RegisterKeyMappingsEvent event;

    public NeoForgeKeyMappingRegistrar(RegisterKeyMappingsEvent event) {
        this.event = event;
    }

    @Override
    public void register(KeyMapping mapping) {
        event.register(mapping);
    }
}
