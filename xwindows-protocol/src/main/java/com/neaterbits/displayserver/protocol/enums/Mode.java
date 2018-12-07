package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class Mode {

    private static BYTE make(int mode) {
        return new BYTE((byte)mode);
    }
    
    public static final int REPLACE = 0;
    public static final int PREPEND = 1;
    public static final int APPEND  = 2;
    
    public static final BYTE Replace = make(REPLACE);
    public static final BYTE Prepend = make(PREPEND);
    public static final BYTE Append  = make(APPEND);
    
}
