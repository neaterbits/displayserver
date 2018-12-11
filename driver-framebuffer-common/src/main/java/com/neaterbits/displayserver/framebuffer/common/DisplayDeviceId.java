package com.neaterbits.displayserver.framebuffer.common;

import java.util.Objects;

public final class DisplayDeviceId {

    private final String deviceId;
    private final Alignment alignment;
    
    public DisplayDeviceId(String deviceId, Alignment alignment) {

        Objects.requireNonNull(deviceId);
        Objects.requireNonNull(alignment);
        
        this.deviceId = deviceId;
        this.alignment = alignment;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Alignment getAlignment() {
        return alignment;
    }
}
