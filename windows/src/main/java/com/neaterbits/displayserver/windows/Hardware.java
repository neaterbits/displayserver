package com.neaterbits.displayserver.windows;

import java.util.Objects;

import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;

public class Hardware {

    private final InputDriver inputDriver;
    private final GraphicsDriver graphicsDriver;

    public Hardware(InputDriver inputDriver, GraphicsDriver graphicsDriver) {
    
        Objects.requireNonNull(inputDriver);
        Objects.requireNonNull(graphicsDriver);
        
        this.inputDriver = inputDriver;
        this.graphicsDriver = graphicsDriver;
    }

    public final InputDriver getInputDriver() {
        return inputDriver;
    }

    public final GraphicsDriver getGraphicsDriver() {
        return graphicsDriver;
    }
}
