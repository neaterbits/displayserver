package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public final class StackMode extends ByteEnum {

    public static final int ABOVE       = 0;
    public static final int BELOW       = 1;
    public static final int TOP_IF      = 2;
    public static final int BOTTOM_IF   = 3;
    public static final int OPPOSITE    = 4;
    
    public static final BYTE Above      = make(ABOVE);
    public static final BYTE Below      = make(BELOW);
    public static final BYTE TopIf      = make(TOP_IF);
    public static final BYTE BottomIf   = make(BOTTOM_IF);
    public static final BYTE Opposite   = make(OPPOSITE);
}
