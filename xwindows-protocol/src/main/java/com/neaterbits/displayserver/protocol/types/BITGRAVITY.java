package com.neaterbits.displayserver.protocol.types;

public final class BITGRAVITY {

    private static BITGRAVITY make(int value) {
        return new BITGRAVITY((byte)value);
    }
    
    public static final int FORGET      = 0;
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
    
    public static final BITGRAVITY Forget       = make(FORGET);
    public static final BITGRAVITY NorthWest    = make(NORTH_WEST);
    public static final BITGRAVITY North        = make(NORTH);
    public static final BITGRAVITY NorthEast    = make(NORTH_EAST);
    public static final BITGRAVITY West         = make(WEST);
    public static final BITGRAVITY Center       = make(CENTER);
    public static final BITGRAVITY East         = make(EAST);
    public static final BITGRAVITY SouthWest    = make(SOUTH_WEST);
    public static final BITGRAVITY South        = make(SOUTH);
    public static final BITGRAVITY SouthEast    = make(SOUTH_EAST);
    public static final BITGRAVITY Static       = make(STATIC);
    
    private final byte value;

    public BITGRAVITY(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
