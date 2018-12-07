package com.neaterbits.displayserver.server;

import com.neaterbits.displayserver.buffers.ImageBuffer;

final class XPixmap extends XDrawable {

    private final ImageBuffer imageBuffer;

    XPixmap(ImageBuffer imageBuffer) {
        this.imageBuffer = imageBuffer;
    }

    ImageBuffer getImageBuffer() {
        return imageBuffer;
    }
}
