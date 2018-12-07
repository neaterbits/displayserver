package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class ArcMode extends ByteEnum {

    public static final int CHORD     = 0;
    public static final int PIE_SLICE = 1;
    
    public static final BYTE Chord    = make(CHORD);
    public static final BYTE PieSlice = make(PIE_SLICE);
}
