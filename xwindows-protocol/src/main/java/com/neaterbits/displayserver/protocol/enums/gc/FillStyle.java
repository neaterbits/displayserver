package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class FillStyle extends ByteEnum {

    public static final int SOLID           = 0;
    public static final int TILED           = 1;
    public static final int OPAQUE_STIPPLED = 2;
    public static final int STIPPLED        = 3;
    
    public static final BYTE Solid          = make(SOLID);
    public static final BYTE Tiled          = make(TILED);
    public static final BYTE OpaqueStippled = make(OPAQUE_STIPPLED);
    public static final BYTE Stippled       = make(STIPPLED);
    
}
