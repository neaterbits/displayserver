package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class MapState extends ByteEnum {

    public static final int UNMAPPED    = 0;
    public static final int UNVIEWABLE  = 1;
    public static final int VIEWABLE    = 2;
    
    public static final BYTE Unmapped   = make(UNMAPPED);
    public static final BYTE Unviewable = make(UNVIEWABLE);
    public static final BYTE Viewable   = make(VIEWABLE);
}
