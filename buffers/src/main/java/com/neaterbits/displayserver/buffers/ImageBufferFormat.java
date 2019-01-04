package com.neaterbits.displayserver.buffers;

import java.util.Objects;

public final class ImageBufferFormat {

    private final PixelFormat pixelFormat;
    private final int scanlinePadBits;

    public ImageBufferFormat(PixelFormat pixelFormat, int scanlinePadBits) {

        Objects.requireNonNull(pixelFormat);

        if (scanlinePadBits % 8 != 0) {
            throw new IllegalArgumentException("scanline pad not on byte boundary");
        }
        
        this.pixelFormat = pixelFormat;
        this.scanlinePadBits = scanlinePadBits;
    }

    public PixelFormat getPixelFormat() {
        return pixelFormat;
    }

    public int getScanlinePadBits() {
        return scanlinePadBits;
    }
}
