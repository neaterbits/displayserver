package com.neaterbits.displayserver.render.cairo;

public class CairoPNGSurface {

    public static CairoStatus writePNG(CairoSurface surface, String fileName) {
        final int status = CairoNative.cairo_surface_write_to_png(surface.getCairoReference(), fileName);
    
        return CairoStatus.fromCairoValue(status);
    }
    
}
