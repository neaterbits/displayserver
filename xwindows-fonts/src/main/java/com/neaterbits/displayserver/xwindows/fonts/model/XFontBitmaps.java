package com.neaterbits.displayserver.xwindows.fonts.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class XFontBitmaps {

    private final FontBitmapFormat bitmapFormat;
    private final List<byte[]> bitmaps;

    public XFontBitmaps(FontBitmapFormat bitmapFormat, List<byte[]> bitmaps) {
        
        Objects.requireNonNull(bitmapFormat);
        Objects.requireNonNull(bitmaps);
        
        this.bitmapFormat = bitmapFormat;
        this.bitmaps = Collections.unmodifiableList(bitmaps);
    }

    public FontBitmapFormat getBitmapFormat() {
        return bitmapFormat;
    }

    public List<byte[]> getBitmaps() {
        return bitmaps;
    }
}
