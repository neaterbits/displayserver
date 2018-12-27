package com.neaterbits.displayserver.xwindows.model;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.BufferOperations;
import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.protocol.types.VISUALID;

public final class XPixmap extends XDrawable {

    private final OffscreenBuffer offscreenBuffer;

    public XPixmap(VISUALID visual, OffscreenBuffer offscreenBuffer) {
        
        super(visual);
        
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
