package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public final class GrabPointerStatus extends ByteEnum {

    public static final int SUCCESS         = 0;
    public static final int ALREADY_GRABBED = 1;
    public static final int INVALID_TIME    = 2;
    public static final int NOT_VIEWABLE    = 3;
    public static final int FROZEN          = 4;

    public static final BYTE Success        = make(SUCCESS);
    public static final BYTE AlreadyGrabbed = make(ALREADY_GRABBED);
    public static final BYTE InvalidType    = make(INVALID_TIME);
    public static final BYTE NotViewable    = make(NOT_VIEWABLE);
    public static final BYTE Frozen         = make(FROZEN);
    
}
