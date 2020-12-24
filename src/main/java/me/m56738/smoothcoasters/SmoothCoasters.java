package me.m56738.smoothcoasters;

import io.netty.buffer.Unpooled;
import me.m56738.smoothcoasters.implementation.Implementation;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.C2SPlayChannelEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternion;

import java.util.NoSuchElementException;

public class SmoothCoasters implements ModInitializer {
    private static final Identifier HANDSHAKE = new Identifier("smoothcoasters", "hs");
    private static SmoothCoasters instance;
    private Implementation currentImplementation;
    private String version;
    private boolean registered;

    public static SmoothCoasters getInstance() {
        return instance;
    }

    public String getVersion() {
        return version;
    }

    public byte getNetworkVersion() {
        return currentImplementation != null ? currentImplementation.getVersion() : 0;
    }

    @Override
    public void onInitialize() {
        instance = this;
        version = FabricLoader.getInstance().getModContainer("smoothcoasters")
                .orElseThrow(NoSuchElementException::new).getMetadata().getVersion().getFriendlyString();
        C2SPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
            if (!registered && channels.contains(HANDSHAKE)) {
                ClientPlayNetworking.registerReceiver(HANDSHAKE, this::handleHandshake);
                registered = true;
            }
        });
    }

    private void handleHandshake(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        byte[] versions = buf.readByteArray();
        client.execute(() -> performHandshake(versions));
    }

    private void setCurrentImplementation(Implementation implementation) {
        if (currentImplementation != null) {
            currentImplementation.unregister();
        }

        currentImplementation = implementation;

        if (currentImplementation != null) {
            PacketByteBuf response = new PacketByteBuf(Unpooled.buffer());
            response.writeByte(currentImplementation.getVersion());
            response.writeString(version);
            ClientPlayNetworking.send(HANDSHAKE, response);
            currentImplementation.register();
        }
    }

    private Implementation findImplementation(byte[] offeredVersions) {
        for (Implementation implementation : Implementation.IMPLEMENTATIONS) {
            byte version = implementation.getVersion();
            for (byte offeredVersion : offeredVersions) {
                if (offeredVersion == version) {
                    return implementation;
                }
            }
        }
        return null;
    }

    private void performHandshake(byte[] offeredVersions) {
        setCurrentImplementation(findImplementation(offeredVersions));
    }

    public void onDisconnected() {
        setCurrentImplementation(null);
        if (registered) {
            ClientPlayNetworking.unregisterReceiver(HANDSHAKE);
            registered = false;
        }
    }

    public void resetRotation() {
        setRotation(Quaternion.IDENTITY, 0);
    }

    public void setRotation(Quaternion rotation, int ticks) {
        ((Rotatable) MinecraftClient.getInstance().worldRenderer).scSetRotation(rotation, ticks);
    }

    public void setEntityRotation(int entityId, Quaternion rotation, int ticks) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world != null) {
            Entity entity = world.getEntityById(entityId);
            if (entity != null) {
                ((Rotatable) entity).scSetRotation(rotation, ticks);
            }
        }
    }
}
