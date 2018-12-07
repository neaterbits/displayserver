package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class CapStyle extends ByteEnum {

    public static final int NOT_LAST    = 0;
    public static final int BUTT        = 1;
    public static final int ROUND       = 2;
    public static final int PROJECTING  = 3;
    
    public static final BYTE NotLast    = make(NOT_LAST);
    public static final BYTE Butt       = make(BUTT);
    public static final BYTE Round      = make(ROUND);
    public static final BYTE Projecting = make(PROJECTING);
}
