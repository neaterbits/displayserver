package com.neaterbits.displayserver.xwindows.fonts;

import java.util.Objects;

public final class FontDescriptor {

    private final String fontName;
    private final XLFD xlfd;
    
    public FontDescriptor(String fontName) {
        
        Objects.requireNonNull(fontName);
        
        this.fontName = fontName;
        this.xlfd = null;
    }

    public FontDescriptor(String fontName, XLFD xlfd) {
        this.fontName = fontName;
        this.xlfd = xlfd;
    }

    public String getFontName() {
        return fontName;
    }

    public XLFD getXlfd() {
        return xlfd;
    }
}
