package com.neaterbits.displayserver.server.render.cairo;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.BYTE;
import com.neaterbits.displayserver.protocol.types.POINT;
import com.neaterbits.displayserver.render.cairo.Cairo;
import com.neaterbits.displayserver.render.cairo.CairoSurface;
import com.neaterbits.displayserver.xwindows.model.XGC;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

final class CairoXLibRenderer implements XLibRenderer {

    private final CairoSurface surface;
    private final Cairo cr;
    
    CairoXLibRenderer(CairoSurface surface) {
        Objects.requireNonNull(surface);
        
        this.surface = surface;
        
        this.cr = new Cairo(surface);
    }

    private void applyGC(XGC gc) {
        
    }
    
    @Override
    public void polyLine(XGC gc, BYTE coordinateMode, POINT[] points) {

        applyGC(gc);
        
        if (points.length != 0) {
            
        }
    }

    @Override
    public void dispose() {
        cr.dispose();
        surface.dispose();
    }
}
