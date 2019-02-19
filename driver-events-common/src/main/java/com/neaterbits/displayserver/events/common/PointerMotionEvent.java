package com.neaterbits.displayserver.events.common;

import com.neaterbits.displayserver.driver.common.DisplayDeviceId;

public final class PointerMotionEvent extends PointerEvent {

    private final int x;
    private final int y;
    
    private final DisplayDeviceId displayDeviceId;
    
    public PointerMotionEvent(int x, int y, DisplayDeviceId displayDeviceId) {
        this.x = x;
        this.y = y;
        this.displayDeviceId = displayDeviceId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public DisplayDeviceId getDisplayDeviceId() {
        return displayDeviceId;
    }
}
