package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.CARD16;

public class WindowClass {

    public static final int COPY_FROM_PARENT    = 0;
    public static final int INPUT_OUTPUT        = 1;
    public static final int INPUT_ONLY          = 2;

    public static final CARD16 CopyFromParent   = new CARD16(COPY_FROM_PARENT);
    public static final CARD16 InputOutput      = new CARD16(INPUT_OUTPUT);
    public static final CARD16 InputOnly        = new CARD16(INPUT_ONLY);
}
