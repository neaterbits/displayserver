package com.neaterbits.displayserver.xwindows.util;

public class Unsigned {

    public static int byteToUnsigned(byte value) {
        
        final int unsigned;
        
        if (value < 0) {
            unsigned = (value & (byte)0x7F) + 0x80;
        }
        else {
            unsigned = value;
        }
        
        return unsigned;
    }

    public static int shortToUnsigned(short value) {
        final int unsigned;
        
        if (value < 0) {
            unsigned = (value & (short)0x7FFF) + 0x8000;
        }
        else {
            unsigned = value;
        }
        
        return unsigned;
    }

    public static long intToUnsigned(int value) {
        
        final long unsigned;
        
        if (value < 0) {
            unsigned = ((value & (int)0x7FFFFFFF) + 0x80000000L);
        }
        else {
            unsigned = value;
        }

        return unsigned;
    }
}
