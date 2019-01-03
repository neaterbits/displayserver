package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.render.cairo.CairoImageSurface;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBuffer;

public final class CairoFontBuffer implements FontBuffer {

    private final CairoImageSurface surface;
    
    CairoFontBuffer(CairoImageSurface surface) {
        
        Objects.requireNonNull(surface);
        
        this.surface = surface;
    }

    CairoImageSurface getSurface() {
        return surface;
    }
    
    @Override
    public void dispose() {
        surface.dispose();
    }
}