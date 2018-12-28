package com.neaterbits.displayserver.render.cairo;

public class CairoSurface extends CairoReference {

    protected CairoSurface(long reference) {
        super(reference);
    }

    public void flush() {
        CairoNative.cairo_surface_flush(getCairoReference());
    }
    
    @Override
    public final void dispose() {
        CairoNative.cairo_surface_destroy(getReference());
    }
}
