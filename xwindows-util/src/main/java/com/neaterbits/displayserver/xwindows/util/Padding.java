package com.neaterbits.displayserver.xwindows.util;

public class Padding {

    public static int getPadding(int length, int padSize) {
        final int pad = (padSize - (length % padSize)) % padSize;

        return pad;
    }
    
    public static int getPaddedLength(int length, int padSize) {
        return length + getPadding(length, padSize);
    }
}
