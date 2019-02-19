package com.neaterbits.displayserver.events.common;

public final class ModifierMapping {

    private final short [] scancodes;

    public ModifierMapping(short[] scancodes) {
        this.scancodes = scancodes;
    }

    public short[] getScancodes() {
        return scancodes;
    }
}
