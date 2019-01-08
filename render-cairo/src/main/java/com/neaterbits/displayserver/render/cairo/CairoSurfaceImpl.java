package com.neaterbits.displayserver.render.cairo;

public class CairoSurfaceImpl extends CairoReference implements CairoSurface {

    protected CairoSurfaceImpl(long reference) {
        super(reference);
    }

    int getReferenceCount() {
        return CairoNative.cairo_surface_get_reference_count(getCairoReference());
    }
    
    @Override
    public CairoStatus writeToPNG(String fileName) {
        return CairoPNGSurface.writePNG(this, fileName);
    }

    @Override
    public void flush() {
        CairoNative.cairo_surface_flush(getCairoReference());
    }

    @Override
    public Cairo createContext() {
        return new CairoImpl(this);
    }
    
    
    @Override
    public void dispose() {
        CairoNative.cairo_surface_destroy(getReference());
    }
}
