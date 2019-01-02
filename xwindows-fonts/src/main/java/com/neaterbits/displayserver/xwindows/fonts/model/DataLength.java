package com.neaterbits.displayserver.xwindows.fonts.model;

public enum DataLength {

    BYTE(1),
    SHORT(2),
    INT(4);
    
    private final int bytes;
    
    private DataLength(int bytes) {
        this.bytes = bytes;
    }

    public int getBytes() {
        return bytes;
    }

    public static DataLength fromStride(int stride) {
        
        final DataLength dataLength;
        
        if (stride % 4 == 0) {
            dataLength = INT;
        }
        else if (stride % 2 == 0) {
            dataLength = SHORT;
        }
        else {
            dataLength = BYTE;
        }
        
        return dataLength;
    }
}
