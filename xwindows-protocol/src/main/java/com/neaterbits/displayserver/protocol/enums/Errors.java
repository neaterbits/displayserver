package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public class Errors {

    private static BYTE make(int error) {
        return new BYTE((byte)error);
    }
    
    public static final int WINDOW = 3;
    
    
    public static final BYTE Window = make(WINDOW);
}
