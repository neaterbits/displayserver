package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class CoordinateMode extends ByteEnum {

    public static final int ORIGIN      = 0;
    public static final int PREVIOUS    = 1;
    
    public static final BYTE Origin     = make(ORIGIN);
    public static final BYTE Previous   = make(PREVIOUS);
}
