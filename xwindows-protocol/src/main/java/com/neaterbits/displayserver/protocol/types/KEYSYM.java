package com.neaterbits.displayserver.protocol.types;

public final class KEYSYM {

    public static final KEYSYM NoSymbol = new KEYSYM(0x00000000);
    public static final KEYSYM VoidSymbol = new KEYSYM(0x00FFFFFF);

    private final int value;

    public KEYSYM(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%08x", value);
    }
}
