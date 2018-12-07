package com.neaterbits.displayserver.protocol.enums.gc;

import com.neaterbits.displayserver.protocol.enums.ByteEnum;
import com.neaterbits.displayserver.protocol.types.BYTE;

public class SubwindowMode extends ByteEnum {

    public static final int CLIP_BY_CHILDREN  = 0;
    public static final int INCLUDE_INFERIORS = 1;
    
    public static final BYTE ClipByChildren   = make(CLIP_BY_CHILDREN);
    public static final BYTE IncludeInferiors = make(INCLUDE_INFERIORS);
}
