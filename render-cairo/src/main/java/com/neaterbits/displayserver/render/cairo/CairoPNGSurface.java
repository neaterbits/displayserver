package com.neaterbits.displayserver.render.cairo;

public class CairoPNGSurface {

    static CairoStatus writePNG(CairoSurfaceImpl surface, String fileName) {
        
        final int status = CairoNative.cairo_surface_write_to_png(surface.getCairoReference(), fileName);
    
        return CairoStatus.fromCairoValue(status);
    }
}
