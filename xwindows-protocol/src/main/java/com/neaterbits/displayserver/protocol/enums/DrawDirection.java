package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public final class DrawDirection extends ByteEnum {
    
    public static final int LEFT_TO_RIGHT = 0;
    public static final int RIGHT_TO_LEFT = 1;

    public static final BYTE LeftToRight = make(LEFT_TO_RIGHT);
    public static final BYTE RightToLeft = make(RIGHT_TO_LEFT);
}
