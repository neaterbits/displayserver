package com.neaterbits.displayserver.server;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.xwindows.fonts.FontCache;
import com.neaterbits.displayserver.xwindows.fonts.FontDescriptor;
import com.neaterbits.displayserver.xwindows.fonts.FontLoader;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;
import com.neaterbits.displayserver.xwindows.fonts.NoSuchFontException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;

final class XFonts {

    private final FontLoader fontLoader;
    private final FontCache fontCache;
    
    XFonts(FontLoaderConfig config) {
        
        this.fontLoader = new FontLoader(config);
        this.fontCache = new FontCache();
    }
    
    XFont openFont(String fontName, FontBufferFactory fontBufferFactory) throws NoSuchFontException, IOException {
        
        XFont font = fontCache.getFont(fontName);
        
        if (font == null) {
            font = fontLoader.loadFont(fontName, fontBufferFactory);
            
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
    
    FontDescriptor [] listFonts(String pattern) throws ValueException {
        return fontLoader.listFonts(pattern);
    }
}
