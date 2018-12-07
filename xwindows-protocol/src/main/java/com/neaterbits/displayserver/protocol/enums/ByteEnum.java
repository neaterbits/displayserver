package com.neaterbits.displayserver.protocol.enums;

import com.neaterbits.displayserver.protocol.types.BYTE;

public abstract class ByteEnum {

    protected static BYTE make(int value) {
        return new BYTE((byte)value);
    }
}
