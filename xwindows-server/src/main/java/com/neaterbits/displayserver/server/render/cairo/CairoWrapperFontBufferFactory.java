package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.windows.compositor.CairoSurfaceWrapper;
import com.neaterbits.displayserver.windows.compositor.NoopCoordinateTranslator;
import com.neaterbits.displayserver.xwindows.fonts.model.FontBitmapFormat;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;

public final class CairoWrapperFontBufferFactory implements FontBufferFactory {

    private final CairoFontBufferFactory delegate;

    public CairoWrapperFontBufferFactory(CairoFontBufferFactory delegate) {

        Objects.requireNonNull(delegate);
        
        this.delegate = delegate;
    }

    @Override
    public FontBuffer createFontBuffer(int glyphIndex, byte[] bitmap, FontBitmapFormat fontBitmapFormat, int width, int height) {
        
        final CairoFontBuffer fontBuffer = (CairoFontBuffer)delegate.createFontBuffer(
                glyphIndex,
                bitmap,
                fontBitmapFormat, 
                width,
                height,
                surface -> new CairoSurfaceWrapper(surface, NoopCoordinateTranslator.INSTANCE));
        
        return fontBuffer;
    }
}
