package com.neaterbits.displayserver.render.cairo;

public class CairoSurface extends CairoReference {

    protected CairoSurface(long reference) {
        super(reference);
    }

    public int getReferenceCount() {
        return CairoNative.cairo_surface_get_reference_count(getCairoReference());
    }
    
    public void flush() {
        CairoNative.cairo_surface_flush(getCairoReference());
    }
    
    @Override
    public void dispose() {
        CairoNative.cairo_surface_destroy(getReference());
    }
}
