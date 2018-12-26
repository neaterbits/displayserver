package com.neaterbits.displayserver.protocol.types;

public final class BUTTON {

    private final short value;

    public BUTTON(short value) {
        
        if (value > 0xFF) {
            throw new IllegalArgumentException();
        }
        
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
