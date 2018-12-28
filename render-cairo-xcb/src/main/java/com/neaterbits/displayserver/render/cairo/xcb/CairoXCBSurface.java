package com.neaterbits.displayserver.render.cairo.xcb;

import com.neaterbits.displayserver.render.cairo.CairoSurface;

public final class CairoXCBSurface extends CairoSurface {

    private final XCBConnection connection;
    
    public static CairoXCBSurface create(XCBConnection connection, int drawable, XCBVisual visual, int width, int height) {

        final long surface = XCBNative.cairo_create_xcb_surface(
                connection.getXCBReference(),
                drawable,
                visual.getXCBReference(),
                width,
                height);
        
        return surface != 0L ? new CairoXCBSurface(surface, connection) : null;
    }
    
    private CairoXCBSurface(long reference, XCBConnection connection) {
        super(reference);

        this.connection = connection;
    }

    @Override
    public void flush() {
        super.flush();
        
        connection.flush();
    }
}
