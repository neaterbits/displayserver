package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.windows.DisplayAreas;
import com.neaterbits.displayserver.windows.compositor.Compositor;
import com.neaterbits.displayserver.xwindows.fonts.render.FontBufferFactory;
import com.neaterbits.displayserver.xwindows.model.render.XLibRendererFactory;

public final class XRendering {
    
    private final DisplayAreas displayAreas;
    private final Compositor compositor;
    private final XLibRendererFactory rendererFactory;
    private final FontBufferFactory fontBufferFactory;
    
    public XRendering(DisplayAreas displayAreas, Compositor compositor, XLibRendererFactory rendererFactory, FontBufferFactory fontBufferFactory) {
    
        Objects.requireNonNull(displayAreas);
        Objects.requireNonNull(compositor);
        Objects.requireNonNull(rendererFactory);
        Objects.requireNonNull(fontBufferFactory);
        
        this.displayAreas = displayAreas;
        this.compositor = compositor;
        this.rendererFactory = rendererFactory;
        this.fontBufferFactory = fontBufferFactory;
    }

    DisplayAreas getDisplayAreas() {
        return displayAreas;
    }

    public Compositor getCompositor() {
        return compositor;
    }

    public XLibRendererFactory getRendererFactory() {
        return rendererFactory;
    }

    public FontBufferFactory getFontBufferFactory() {
        return fontBufferFactory;
    }
}
