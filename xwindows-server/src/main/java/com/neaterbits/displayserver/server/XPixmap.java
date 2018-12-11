package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.buffers.OffscreenBuffer;

final class XPixmap extends XDrawable {

    private final OffscreenBuffer imageBuffer;

    XPixmap(OffscreenBuffer imageBuffer) {
        this.imageBuffer = imageBuffer;
    }

    OffscreenBuffer getImageBuffer() {
        return imageBuffer;
    }
}
