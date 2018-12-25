package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.events.common.InputDriver;
import com.neaterbits.displayserver.events.common.ModifierScancodes;
import com.neaterbits.displayserver.framebuffer.common.GraphicsDriver;

public final class XHardware {
    private final InputDriver inputDriver;
    private final GraphicsDriver graphicsDriver;
    private final ModifierScancodes modifierScancodes;

    public XHardware(InputDriver inputDriver, GraphicsDriver graphicsDriver, ModifierScancodes modifierScancodes) {
    
        Objects.requireNonNull(inputDriver);
        Objects.requireNonNull(graphicsDriver);
        Objects.requireNonNull(modifierScancodes);
        
        this.inputDriver = inputDriver;
        this.graphicsDriver = graphicsDriver;
        this.modifierScancodes = modifierScancodes;
    }

    InputDriver getInputDriver() {
        return inputDriver;
    }

    GraphicsDriver getGraphicsDriver() {
        return graphicsDriver;
    }

    public ModifierScancodes getModifierScancodes() {
        return modifierScancodes;
    }
}
