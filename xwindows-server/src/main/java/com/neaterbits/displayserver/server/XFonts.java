package com.neaterbits.displayserver.server;

import java.io.IOException;

import com.neaterbits.displayserver.protocol.exception.ValueException;
import com.neaterbits.displayserver.xwindows.fonts.FontCache;
import com.neaterbits.displayserver.xwindows.fonts.FontDescriptor;
import com.neaterbits.displayserver.xwindows.fonts.FontLoader;
import com.neaterbits.displayserver.xwindows.fonts.FontLoaderConfig;
import com.neaterbits.displayserver.xwindows.fonts.NoSuchFontException;
import com.neaterbits.displayserver.xwindows.fonts.model.XFont;
import com.neaterbits.displayserver.xwindows.fonts.model.XNamedFontModel;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;

public final class XFonts {

    private final FontLoader fontLoader;
    private final FontCache fontCache;
    
    public XFonts(FontLoaderConfig config) {
        this.fontLoader = new FontLoader(config);
        this.fontCache = new FontCache();
    }
    
    public XFont openFont(String pattern, FontBufferFactory fontBufferFactory) throws NoSuchFontException, IOException, ValueException {

        final FontDescriptor [] fonts = fontLoader.listFonts(pattern);

        XFont font;
        
        if (fonts.length > 0) {
            
            final FontDescriptor fontDescriptor = fonts[0];
            final String fontName = fontDescriptor.getFontName();
            
            font = fontCache.getFont(fontName);
            
            if (font == null) {
                
                font = fontLoader.loadFont(fontDescriptor, fontBufferFactory);
                
                if (font == null) {
                    throw new IllegalStateException(); // Exception if font not found
                }
                
                fontCache.add(fontName, font);
            }
            else {
                font.addRef();
            }
        }
        else {
            throw new NoSuchFontException("No such font");
        }
        
        return font;
    }
    
    public void closeFont(XFont font) {
        
        final boolean referenced = font.remRef();
        
        if (referenced) {
            fontCache.remove(font.getName());
        }
    }
    
    public FontDescriptor [] listFonts(String pattern) throws ValueException {
        return fontLoader.listFonts(pattern);
    }

    public XNamedFontModel [] listFontsWithInfo(String pattern) throws ValueException {
        return fontLoader.listFontsWithInfo(pattern);
    }
}
