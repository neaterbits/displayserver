package com.neaterbits.displayserver.xwindows.fonts.model;

import java.util.Objects;

public final class XFontEncodings {

    private final short minCharOrByte2;
    private final short maxCharOrByte2;
    private final short minByte1;
    private final short maxByte1;
    private final short defaultChar;
    private final short[] glyphIndices;

    public XFontEncodings(
            short minCharOrByte2, short maxCharOrByte2,
            short minByte1, short maxByte1,
            short defaultChar,
            short[] glyphIndices) {
        
        Objects.requireNonNull(glyphIndices);
        
        this.minCharOrByte2 = minCharOrByte2;
        this.maxCharOrByte2 = maxCharOrByte2;
        this.minByte1 = minByte1;
        this.maxByte1 = maxByte1;
        this.defaultChar = defaultChar;
        this.glyphIndices = glyphIndices;
    }

    public short getMinCharOrByte2() {
        return minCharOrByte2;
    }

    public short getMaxCharOrByte2() {
        return maxCharOrByte2;
    }

    public short getMinByte1() {
        return minByte1;
    }

    public short getMaxByte1() {
        return maxByte1;
    }

    public short getDefaultChar() {
        return defaultChar;
    }

    public short[] getGlyphIndices() {
        return glyphIndices;
    }

    @Override
    public String toString() {
        return "XFontEncodings [minCharOrByte2=" + minCharOrByte2 + ", maxCharOrByte2=" + maxCharOrByte2 + ", minByte1="
                + minByte1 + ", maxByte1=" + maxByte1 + ", defaultChar=" + defaultChar + ", glyphIndices="
                + glyphIndices.length + "]";
    }
}
