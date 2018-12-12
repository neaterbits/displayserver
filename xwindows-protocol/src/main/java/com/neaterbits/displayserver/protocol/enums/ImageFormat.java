package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class ImageFormat extends ByteEnum {

    public static final int BITMAP   = 0;
    public static final int XYPIXMAP = 1;
    public static final int ZPIXMAP  = 2;
    
    public static final BYTE BitMap     = make(BITMAP);
    public static final BYTE XYPixMap   = make(XYPIXMAP);
    public static final BYTE ZPixMap    = make(ZPIXMAP);
    
}
