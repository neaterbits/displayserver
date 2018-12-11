package com.neaterbits.displayserver.windows.config;

import java.util.Objects;

import com.neaterbits.displayserver.framebuffer.common.Alignment;
import com.neaterbits.displayserver.framebuffer.common.DisplayDeviceId;

public final class DisplayConfig {

    private final DisplayDeviceId deviceId;
    private final Alignment alignment;

    public DisplayConfig(DisplayDeviceId deviceId, Alignment alignment) {
        Objects.requireNonNull(deviceId);
        Objects.requireNonNull(alignment);

        this.deviceId = deviceId;
        this.alignment = alignment;
    }

    public DisplayDeviceId getDeviceId() {
        return deviceId;
    }

    public Alignment getAlignment() {
        return alignment;
    }
}
