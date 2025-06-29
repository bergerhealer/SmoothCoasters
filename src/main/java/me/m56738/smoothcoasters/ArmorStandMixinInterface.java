package me.m56738.smoothcoasters;

import net.minecraft.client.render.entity.state.ArmorStandEntityRenderState;

public interface ArmorStandMixinInterface {
    void smoothcoasters$setTicks(int ticks);

    void smoothcoasters$animate(ArmorStandEntityRenderState renderState, float time);
}
