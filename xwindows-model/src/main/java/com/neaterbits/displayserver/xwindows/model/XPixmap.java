package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.protocol.types.VISUALID;
import com.neaterbits.displayserver.xwindows.model.render.XLibRenderer;

public final class XPixmap extends XDrawable {

    private final OffscreenBuffer offscreenBuffer;

    public XPixmap(VISUALID visual, OffscreenBuffer offscreenBuffer, XLibRenderer renderer) {
        
        super(visual, renderer);
        
        Objects.requireNonNull(offscreenBuffer);
        
        this.offscreenBuffer = offscreenBuffer;
    }

    public OffscreenBuffer getOffscreenBuffer() {
        return offscreenBuffer;
    }

    @Override
    public BufferOperations getBufferOperations() {
        return offscreenBuffer;
    }
}
