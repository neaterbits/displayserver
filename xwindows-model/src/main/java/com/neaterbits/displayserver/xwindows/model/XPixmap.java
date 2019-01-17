package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.windows.compositor.OffscreenSurface;
import com.neaterbits.displayserver.windows.compositor.Surface;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public final class XPixmap extends XDrawable {

    private final OffscreenSurface offscreenSurface;

    public XPixmap(VISUALID visual, OffscreenSurface offscreenSurface, XLibRenderer renderer) {
        
        super(visual, renderer);
        
        Objects.requireNonNull(offscreenSurface);
        
        this.offscreenSurface = offscreenSurface;
    }

    public OffscreenSurface getOffscreenSurface() {
        return offscreenSurface;
    }

    @Override
    public Surface getSurface() {
        return offscreenSurface;
    }
}
