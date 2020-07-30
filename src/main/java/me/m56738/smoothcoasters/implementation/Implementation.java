package me.m56738.smoothcoasters.implementation;

public interface Implementation {
    Implementation[] IMPLEMENTATIONS = new Implementation[]{
            new ImplV2(),
            new ImplV1(),
    };

    byte getVersion();

    void register();

    void unregister();
}
