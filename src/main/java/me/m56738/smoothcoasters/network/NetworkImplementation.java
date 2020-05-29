package me.m56738.smoothcoasters.network;

public interface NetworkImplementation {
    NetworkImplementation[] IMPLEMENTATIONS = new NetworkImplementation[]{
            new NetworkV1(),
    };

    byte getVersion();

    void register();

    void unregister();
}
