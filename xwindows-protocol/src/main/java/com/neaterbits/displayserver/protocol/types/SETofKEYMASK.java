package com.neaterbits.displayserver.protocol.types;

public final class SETofKEYMASK {

    private final short value;

    public SETofKEYMASK(short value) {
        this.value = value;
    }

    public short getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%04x", value);
    }
}
