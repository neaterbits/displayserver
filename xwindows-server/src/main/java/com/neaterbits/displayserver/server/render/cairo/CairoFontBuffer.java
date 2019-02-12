package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.render.cairo.CairoSurface;
import com.neaterbits.displayserver.types.Size;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;

public final class CairoFontBuffer implements FontBuffer {

    private final CairoSurface surface;
    
    CairoFontBuffer(CairoSurface surface) {
        
        Objects.requireNonNull(surface);
        
        this.surface = surface;
    }

    CairoSurface getSurface() {
        return surface;
    }

    @Override
    public Size getSize() {
        return new Size(surface.getWidth(), surface.getHeight());
    }

    @Override
    public void flush() {
        surface.flush();
    }

    @Override
    public void dispose() {
        surface.dispose();
    }
}
