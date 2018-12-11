package com.neaterbits.displayserver.framebuffer.common;

import java.util.Objects;

import com.neaterbits.displayserver.types.Size;

public final class DisplayMode {

    private final Size resolution;
    private final int depth;
    private final float refreshRate;
    
    public DisplayMode(Size resolution, int depth, float refreshRate) {

        Objects.requireNonNull(resolution);
        
        this.resolution = resolution;
        this.depth = depth;
        this.refreshRate = refreshRate;
    }

    public Size getResolution() {
        return resolution;
    }

    public int getDepth() {
        return depth;
    }

    public float getRefreshRate() {
        return refreshRate;
    }
}
