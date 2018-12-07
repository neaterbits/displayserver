package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public final class Alloc extends ByteEnum {

    public static final int NONE = 0;
    public static final int ALL  = 1;
    
    public static final BYTE None = make(NONE);
    public static final BYTE All  = make(ALL);
    
    public static String name(BYTE alloc) {
        
        final String name;
        
        switch (alloc.getValue()) {
        case NONE: name = "None"; break;
        case ALL:  name = "All"; break;
        
        default:
            throw new UnsupportedOperationException();
        }
        
        return name;
    }
}
