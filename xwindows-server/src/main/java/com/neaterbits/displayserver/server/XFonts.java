package com.neaterbits.displayserver.server;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import com.neaterbits.displayserver.protocol.types.ATOM;
import com.neaterbits.displayserver.xwindows.fonts.FontCache;
import com.neaterbits.displayserver.xwindows.fonts.FontLoader;
import com.neaterbits.displayserver.xwindows.fonts.NoSuchFontException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;

final class XFonts {

    private final FontLoader fontLoader;
    private final FontCache fontCache;
    
    XFonts(List<String> fontPaths, Function<String, ATOM> getAtom) {
        
        this.fontLoader = new FontLoader(fontPaths, getAtom);
        this.fontCache = new FontCache();
    }
    
    XFont openFont(String fontName) throws NoSuchFontException, IOException {
        
        XFont font = fontCache.getFont(fontName);
        
        if (font == null) {
            font = fontLoader.loadFont(fontName);
            
            if (font == null) {
                throw new IllegalStateException(); // Exception if font not found
            }
            
            fontCache.add(fontName, font);
        }
        else {
            font.addRef();
        }
        
        return font;
    }
    
    void closeFont(XFont font) {
        
        final boolean referenced = font.remRef();
        
        if (referenced) {
            fontCache.remove(font.getName());
        }
    }
}
