package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;

public final class XRendering {
    
    private final Compositor compositor;
    private final XLibRendererFactory rendererFactory;
    
    public XRendering(Compositor compositor, XLibRendererFactory rendererFactory) {
    
        Objects.requireNonNull(compositor);
        Objects.requireNonNull(rendererFactory);
        
        this.compositor = compositor;
        this.rendererFactory = rendererFactory;
    }

    Compositor getCompositor() {
        return compositor;
    }

    XLibRendererFactory getRendererFactory() {
        return rendererFactory;
    }
}
