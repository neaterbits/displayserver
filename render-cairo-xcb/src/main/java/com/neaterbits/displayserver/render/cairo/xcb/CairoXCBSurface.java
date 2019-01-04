package com.neaterbits.displayserver.render.cairo.xcb;

import java.util.Objects;

import com.neaterbits.displayserver.render.cairo.CairoSurface;

public final class CairoXCBSurface extends CairoSurface {

    private final XCBConnection connection;
    private final int drawable;
    private final DrawableType drawableType;
    
    public static CairoXCBSurface create(XCBConnection connection, int drawable, DrawableType drawableType, XCBVisual visual, int width, int height) {

        final long surface = XCBNative.cairo_create_xcb_surface(
                connection.getXCBReference(),
                drawable,
                visual.getXCBReference(),
                width,
                height);
        
        return surface != 0L ? new CairoXCBSurface(surface, drawable, drawableType, connection) : null;
    }
    
    private CairoXCBSurface(long reference, int drawable, DrawableType drawableType, XCBConnection connection) {
        super(reference);

        Objects.requireNonNull(drawableType);
        
        this.drawable = drawable;
        this.drawableType = drawableType;
        this.connection = connection;
    }

    @Override
    public void flush() {
        super.flush();
        
        connection.flush();
    }

    @Override
    public String toString() {
        return "CairoXCBSurface [connection=" + connection
                + ", drawable=" + String.format("%s@%08x", drawableType.toString(), drawable) + "]";
    }
}
