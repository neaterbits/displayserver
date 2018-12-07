package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class JoinStyle extends ByteEnum {

    public static final int MITER = 0;
    public static final int ROUND = 1;
    public static final int BEVEL = 2;
    
    public static final BYTE Miter = make(MITER);
    public static final BYTE Round = make(ROUND);
    public static final BYTE Bevel = make(BEVEL);
}
