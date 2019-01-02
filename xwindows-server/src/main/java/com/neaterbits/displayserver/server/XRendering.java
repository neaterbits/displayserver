package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;

public final class XRendering {
    
    private final Compositor compositor;
    private final XLibRendererFactory rendererFactory;
    private final FontBufferFactory fontBufferFactory;
    
    public XRendering(Compositor compositor, XLibRendererFactory rendererFactory, FontBufferFactory fontBufferFactory) {
    
        Objects.requireNonNull(compositor);
        Objects.requireNonNull(rendererFactory);
        Objects.requireNonNull(fontBufferFactory);
        
        this.compositor = compositor;
        this.rendererFactory = rendererFactory;
        this.fontBufferFactory = fontBufferFactory;
    }

    Compositor getCompositor() {
        return compositor;
    }

    XLibRendererFactory getRendererFactory() {
        return rendererFactory;
    }

    FontBufferFactory getFontBufferFactory() {
        return fontBufferFactory;
    }
}
