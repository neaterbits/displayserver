package com.neaterbits.displayserver.xwindows.fonts.render;

import com.neaterbits.displayserver.xwindows.fonts.model.FontBitmapFormat;

public interface FontBufferFactory {

    
    // Bitmap in native byte and bit order
    FontBuffer createFontBuffer(int glyphIndex, byte [] bitmap, FontBitmapFormat fontBitmapFormat, int width, int height);
    
}
