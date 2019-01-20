package com.neaterbits.displayserver.protocol.types;

public final class WINGRAVITY {

    private static WINGRAVITY make(int value) {
        return new WINGRAVITY((byte)value);
    }
    
    public static final int UNMAP       = 0;
    public static final int NORTH_WEST  = 1;
    public static final int NORTH       = 2;
    public static final int NORTH_EAST  = 3;
    public static final int WEST        = 4;
    public static final int CENTER      = 5;
    public static final int EAST        = 6;
    public static final int SOUTH_WEST  = 7;
    public static final int SOUTH       = 8;
    public static final int SOUTH_EAST  = 9;
    public static final int STATIC      = 10;
    
    public static final WINGRAVITY Unmap        = make(UNMAP);
    public static final WINGRAVITY NorthWest    = make(NORTH_WEST);
    public static final WINGRAVITY North        = make(NORTH);
    public static final WINGRAVITY NorthEast    = make(NORTH_EAST);
    public static final WINGRAVITY West         = make(WEST);
    public static final WINGRAVITY Center       = make(CENTER);
    public static final WINGRAVITY East         = make(EAST);
    public static final WINGRAVITY SouthWest    = make(SOUTH_WEST);
    public static final WINGRAVITY South        = make(SOUTH);
    public static final WINGRAVITY SouthEast    = make(SOUTH_EAST);
    public static final WINGRAVITY Static       = make(STATIC);
    
    private final byte value;

    public WINGRAVITY(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("%02x", value);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + value;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WINGRAVITY other = (WINGRAVITY) obj;
        if (value != other.value)
            return false;
        return true;
    }
}
