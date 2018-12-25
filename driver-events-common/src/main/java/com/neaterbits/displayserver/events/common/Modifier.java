package com.neaterbits.displayserver.events.common;

public final class Modifier {

    private final short [] scancodes;

    public Modifier(short[] scancodes) {
        this.scancodes = scancodes;
    }

    public short[] getScancodes() {
        return scancodes;
    }
}
