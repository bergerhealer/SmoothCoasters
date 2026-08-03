package me.m56738.smoothcoasters.fabric;

import me.m56738.smoothcoasters.common.KeyMappingRegistrar;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

public class FabricKeyMappingRegistrar implements KeyMappingRegistrar {
    @Override
    public void register(KeyMapping mapping) {
        KeyMappingHelper.registerKeyMapping(mapping);
    }
}
