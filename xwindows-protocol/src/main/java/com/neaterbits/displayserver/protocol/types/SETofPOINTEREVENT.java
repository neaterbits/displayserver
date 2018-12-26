package com.neaterbits.displayserver.protocol.types;

public final class SETofPOINTEREVENT {

    private final int value;

    public SETofPOINTEREVENT(int value) {
        
        if (value > 0xFFFF) {
            throw new IllegalArgumentException();
        }
        
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%04x", value);
    }
}
