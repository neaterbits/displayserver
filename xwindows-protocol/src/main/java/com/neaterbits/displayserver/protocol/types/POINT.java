package com.neaterbits.displayserver.protocol.types;

public final class POINT {

    private final short x;
    private final short y;

    public POINT(INT16 x, INT16 y) {
        this(x.getValue(), y.getValue());
    }

    public POINT(short x, short y) {
        this.x = x;
        this.y = y;
    }

    public short getX() {
        return x;
    }

    public short getY() {
        return y;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
