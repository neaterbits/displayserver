package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public final class RevertTo extends ByteEnum {

    public static final int NONE            = 0;
    public static final int POINTER_ROOT    = 1;
    public static final int PARENT          = 2;
    
    public static final BYTE None           = make(NONE);
    public static final BYTE PointerRoot    = make(POINTER_ROOT);
    public static final BYTE Parent         = make(PARENT);
}
