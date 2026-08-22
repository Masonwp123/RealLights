package com.masonwp123.reallights.client;

public abstract class RealLightSource implements AutoCloseable {

    protected final RealLight light;

    public RealLightSource(RealLight light) {
        this.light = light;
        RealLightsClient.addLight(light);
    }

    @Override
    public void close() {
        RealLightsClient.removeLight(this.light);
    }

}
