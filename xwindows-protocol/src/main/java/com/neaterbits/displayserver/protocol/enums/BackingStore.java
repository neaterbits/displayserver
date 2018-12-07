package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public final class BackingStore extends ByteEnum {

    public static final int NOT_USEFUL  = 0;
    public static final int WHEN_MAPPED = 1;
    public static final int ALWAYS      = 2;
    
    public static final BYTE NotUseful  = make(NOT_USEFUL);
    public static final BYTE WhenMapped = make(WHEN_MAPPED);
    public static final BYTE Always     = make(ALWAYS);
    
}
