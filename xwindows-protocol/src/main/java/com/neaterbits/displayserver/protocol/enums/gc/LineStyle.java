package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class LineStyle extends ByteEnum {

    public static final int SOLID       = 0;
    public static final int ON_OFF_DASH = 1;
    public static final int DOUBLE_DASH = 2;
    
    public static final BYTE Solid      = make(SOLID);
    public static final BYTE OnOffDash  = make(ON_OFF_DASH);
    public static final BYTE DoubleDash = make(DOUBLE_DASH);
}
