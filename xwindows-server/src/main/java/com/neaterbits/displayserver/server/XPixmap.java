package com.neaterbits.displayserver.server;

import java.util.Objects;

import com.neaterbits.displayserver.buffers.OffscreenBuffer;
import com.neaterbits.displayserver.protocol.types.VISUALID;

final class XPixmap extends XDrawable {

    private final OffscreenBuffer offscreenBuffer;

    XPixmap(VISUALID visual, OffscreenBuffer offscreenBuffer) {
        
        super(visual);
        
        Objects.requireNonNull(offscreenBuffer);
        
        this.offscreenBuffer = offscreenBuffer;
    }

    OffscreenBuffer getOffscreenBuffer() {
        return offscreenBuffer;
    }
}
