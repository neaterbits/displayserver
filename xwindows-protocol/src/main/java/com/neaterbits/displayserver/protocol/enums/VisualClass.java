package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class VisualClass extends ByteEnum {

    public static final int STATICGRAY  = 0;
    public static final int GRAYSCALE   = 1;
    public static final int STATICCOLOR = 2;
    public static final int PSEUDOCOLOR = 3;
    public static final int TRUECOLOR   = 4;
    public static final int DIRECTCOLOR = 5;
    
    public static final BYTE StaticGray     = make(STATICGRAY);
    public static final BYTE GrayScale      = make(GRAYSCALE);
    public static final BYTE StaticColor    = make(STATICCOLOR);
    public static final BYTE PseudoColor    = make(PSEUDOCOLOR);
    public static final BYTE TrueColor      = make(TRUECOLOR);
    public static final BYTE DirectColor    = make(DIRECTCOLOR);
}
