package com.neaterbits.displayserver.xwindows.fonts;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class XFontBitmaps {

    private final List<byte[]> bitmaps;

    public XFontBitmaps(List<byte[]> bitmaps) {
        
        Objects.requireNonNull(bitmaps);
        
        this.bitmaps = Collections.unmodifiableList(bitmaps);
    }

    public List<byte[]> getBitmaps() {
        return bitmaps;
    }
}
