package com.neaterbits.displayserver.protocol.types;

public final class SETofKEYBUTMASK {

    public static final int SHIFT   = 0x0001;
    public static final int LOCK    = 0x0002;
    public static final int CONTROL = 0x0004;
    public static final int MOD1    = 0x0008;
    public static final int MOD2    = 0x0010;
    public static final int MOD3    = 0x0020;
    public static final int MOD4    = 0x0040;
    public static final int MOD5    = 0x0080;

    
    private final short value;

    public SETofKEYBUTMASK(short value) {
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
