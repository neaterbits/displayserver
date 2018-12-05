package com.neaterbits.displayserver.buffers;

public abstract class ImageBuffer implements BufferInfo {

	private final int width;
	private final int height;
	private final int bytesPerPixel;

	abstract int getPixelRGBA(int pixelIdx);
	
	abstract void setPixelRGBA(int pixelIdx, int rgba);
	
	public ImageBuffer(int width, int height, int bytesPerPixel) {
		this.width = width;
		this.height = height;
		this.bytesPerPixel = bytesPerPixel;
	}

	@Override
	public final int getWidth() {
		return width;
	}

	@Override
	public final int getHeight() {
		return height;
	}

	@Override
	public final int getBytesPerPixel() {
		return bytesPerPixel;
	}
}
