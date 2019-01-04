package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public abstract class XDrawable extends XResource {

    private final VISUALID visual;

    private final XLibRenderer renderer;
    
    public abstract BufferOperations getBufferOperations();
    
    XDrawable(VISUALID visual, XLibRenderer renderer) {
        
        Objects.requireNonNull(visual);
        Objects.requireNonNull(renderer);
        
        this.visual = visual;
        this.renderer = renderer;
    }
    
    public final XLibRenderer getRenderer() {
        return renderer;
    }

    public final VISUALID getVisual() {
        return visual;
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }
}
