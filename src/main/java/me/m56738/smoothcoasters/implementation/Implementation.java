package me.m56738.smoothcoasters.implementation;

public interface Implementation {
    Implementation[] IMPLEMENTATIONS = new Implementation[]{
            new ImplV5(),
            new ImplV4(),
    };

    byte getVersion();

    void register();

    void unregister();
}
