package com.neaterbits.displayserver.render.cairo;

public class CairoImageSurface extends CairoSurfaceImpl {

    private final long mallocedDataReference;
    
    private final int width;
    private final int height;
    
    public CairoImageSurface(CairoFormat format, int width, int height) {
        super(CairoNative.cairo_image_surface_create(format.getCairoValue(), width, height));

        this.width = width;
        this.height = height;
        
        this.mallocedDataReference = 0L;
    }

    public CairoImageSurface(byte [] data, CairoFormat format, int width, int height, int stride) {
        this(
                CairoNative.cairo_image_surface_create_for_data(data, format.getCairoValue(), width, height, stride),
                width,
                height);
    }
    
    private CairoImageSurface(long [] references, int width, int height) {
        super(references[0]);
        
        this.width = width;
        this.height = height;
        
        this.mallocedDataReference = references[1];
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void dispose() {
        super.dispose();

        CairoNative.free_image_surface_data(mallocedDataReference);
    }
}
