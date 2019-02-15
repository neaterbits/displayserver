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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pixelFormat == null) ? 0 : pixelFormat.hashCode());
        result = prime * result + scanlinePadBits;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageBufferFormat other = (ImageBufferFormat) obj;
        if (pixelFormat != other.pixelFormat)
            return false;
        if (scanlinePadBits != other.scanlinePadBits)
            return false;
        return true;
    }
}
