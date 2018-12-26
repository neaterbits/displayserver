package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;

public final class XHardware {
    private final InputDriver inputDriver;
    private final GraphicsDriver graphicsDriver;

    public XHardware(InputDriver inputDriver, GraphicsDriver graphicsDriver) {
    
        Objects.requireNonNull(inputDriver);
        Objects.requireNonNull(graphicsDriver);
        
        this.inputDriver = inputDriver;
        this.graphicsDriver = graphicsDriver;
    }

    InputDriver getInputDriver() {
        return inputDriver;
    }

    GraphicsDriver getGraphicsDriver() {
        return graphicsDriver;
    }
}
