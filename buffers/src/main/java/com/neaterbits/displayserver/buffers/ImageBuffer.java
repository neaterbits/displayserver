package com.neaterbits.displayserver.buffers;

import com.neaterbits.displayserver.types.Size;

public abstract class ImageBuffer implements BufferInfo, OffscreenBuffer {

    private final Size size;
	private final PixelFormat pixelFormat;

	abstract int getPixelRGBA(int pixelIdx);
	
	abstract void setPixelRGBA(int pixelIdx, int rgba);
	
	public ImageBuffer(int width, int height, PixelFormat pixelFormat) {
	    this.size = new Size(width, height);
	    this.pixelFormat = pixelFormat;
	}

	@Override
	public final int getWidth() {
		return size.getWidth();
	}

	@Override
	public final int getHeight() {
		return size.getHeight();
	}

	@Override
	public final int getBytesPerPixel() {
		return pixelFormat.getBytesPerPixel();
	}

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public int getDepth() {
        return pixelFormat.getDepth();
    }

    @Override
    public PixelFormat getPixelFormat() {
        return pixelFormat;
    }
	
	
}
