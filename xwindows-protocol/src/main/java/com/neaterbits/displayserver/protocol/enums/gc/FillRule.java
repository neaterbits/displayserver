package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class FillRule extends ByteEnum {

    public static final int EVEN_ODD = 0;
    public static final int WINDING  = 1;
    
    public static final BYTE EvenOdd = make(EVEN_ODD);
    public static final BYTE Winding = make(WINDING);
}
