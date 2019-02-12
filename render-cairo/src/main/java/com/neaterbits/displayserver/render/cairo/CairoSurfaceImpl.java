package com.neaterbits.displayserver.render.cairo;

public class CairoSurfaceImpl extends CairoReference implements CairoSurface {

    private final int width;
    private final int height;

    protected CairoSurfaceImpl(long reference, int width, int height) {
        super(reference);
    
        this.width = width;
        this.height = height;
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
    public final Cairo createContext() {
        return new CairoImpl(this);
    }
    
    @Override
    public final int getWidth() {
        return width;
    }

    @Override
    public final int getHeight() {
        return height;
    }

    @Override
    public void dispose() {
        CairoNative.cairo_surface_destroy(getReference());
    }
}
