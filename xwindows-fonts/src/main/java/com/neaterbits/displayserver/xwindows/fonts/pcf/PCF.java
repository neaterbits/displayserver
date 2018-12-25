package com.neaterbits.displayserver.xwindows.fonts.pcf;

class PCF {

    static final int DEFAULT_FORMAT     = 0x00000000;
    static final int INKBOUNDS          = 0x00000200;
    static final int ACCEL_W_INKBOUNDS  = 0x00000100;
    static final int COMPRESSED_METRICS = 0x00000100;
    
    static final int GLYPH_PAD_MASK = 3 << 0;
    static final int BYTE_MASK      = 1 << 2;
    static final int BIT_MASK       = 1 << 3;
    static final int SCAN_UNIT_MASK = 3 << 4;
    
}
