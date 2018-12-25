package com.neaterbits.displayserver.xwindows.fonts.pcf;

final class TOCEntry {

    public static final int PROPERTIES      = (1 << 0);
    public static final int ACCELERATORS    = (1 << 1);
    public static final int METRICS         = (1 << 2);
    public static final int BITMAPS         = (1 << 3);
    public static final int INK_METRICS     = (1 << 4);
    public static final int BDF_ENCODINGS   = (1 << 5);
    public static final int SWIDTHS         = (1 << 6);
    public static final int GLYPH_NAMES     = (1 << 7);
    public static final int BDF_ACCELERATORS = (1 << 8);
    
    private final int type;
    private final int format;
    private final int size;
    private final int offset;
    
    TOCEntry(int type, int format, int size, int offset) {
        this.type = type;
        this.format = format;
        this.size = size;
        this.offset = offset;
    }

    int getType() {
        return type;
    }

    int getFormat() {
        return format;
    }

    int getSize() {
        return size;
    }

    int getOffset() {
        return offset;
    }
}
