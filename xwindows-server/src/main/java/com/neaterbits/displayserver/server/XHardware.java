package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;
import com.neaterbits.displayserver.windows.Hardware;

public final class XHardware extends Hardware {

    public XHardware(InputDriver inputDriver, GraphicsDriver graphicsDriver) {
        super(inputDriver, graphicsDriver);
    }
}

