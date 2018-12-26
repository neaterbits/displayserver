package com.neaterbits.displayserver.protocol.types;

public final class CHAR2B {

    private final byte byte1;
    private final byte byte2;
    
    public CHAR2B(byte byte1, byte byte2) {
        this.byte1 = byte1;
        this.byte2 = byte2;
    }

    public byte getByte1() {
        return byte1;
    }

    public byte getByte2() {
        return byte2;
    }

    @Override
    public String toString() {
        return String.format("%02x%02x", byte1, byte2);
    }
}
